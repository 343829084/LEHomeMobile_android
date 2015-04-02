/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package my.home.lehome.mvp.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Messenger;

import com.squareup.otto.Subscribe;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import my.home.common.BusProvider;
import my.home.domain.events.DSaveAutoCompleteLocalHistoryEvent;
import my.home.domain.events.DShowCmdSuggestionEvent;
import my.home.domain.usecase.MarkCurrentInputUsecaseImpl;
import my.home.domain.usecase.SaveAutoCompleteLocalHistoryUsecaseImpl;
import my.home.lehome.fragment.ChatFragment;
import my.home.lehome.helper.DBHelper;
import my.home.lehome.helper.MessageHelper;
import my.home.lehome.mvp.views.ChatItemListView;
import my.home.lehome.mvp.views.ChatSuggestionView;
import my.home.lehome.mvp.views.SaveLocalHistoryView;
import my.home.lehome.service.SendMsgIntentService;
import my.home.lehome.util.Constants;
import my.home.model.entities.AutoCompleteItem;
import my.home.model.entities.ChatItem;

/**
 * Created by legendmohe on 15/2/19.
 */
public class ChatFragmentPresenter extends MVPPresenter {

    private WeakReference<SaveLocalHistoryView> mSaveLocalHistoryView;
    private WeakReference<ChatItemListView> mChatItemListView;
    private WeakReference<ChatSuggestionView> mChatSuggestionView;

    public ChatFragmentPresenter(SaveLocalHistoryView saveLocalHistoryView
            , ChatItemListView chatItemListView
            , ChatSuggestionView chatSuggestionView) {
        this.mSaveLocalHistoryView = new WeakReference<SaveLocalHistoryView>(saveLocalHistoryView);
        this.mChatItemListView = new WeakReference<ChatItemListView>(chatItemListView);
        this.mChatSuggestionView = new WeakReference<ChatSuggestionView>(chatSuggestionView);
    }

    public void markAndSendCurrentInput(String input, boolean local) {
        if (mChatItemListView.get() == null)
            return;
//        new SendCommandAsyncTask(mChatItemListView.get().getContext(), input, local).execute();
        Context context = mChatItemListView.get().getContext();
        String message;
        String serverURL;
        if (local) {
            message = MessageHelper.getFormatLocalMessage(input);
            serverURL = MessageHelper.getLocalServerURL();
        } else {
            message = MessageHelper.getFormatMessage(input);
            serverURL = MessageHelper.getServerURL(message);
        }
        Intent serviceIntent = new Intent(context, SendMsgIntentService.class);
        serviceIntent.putExtra("local", local);
        serviceIntent.putExtra("cmdString", message);
        serviceIntent.putExtra("cmd", input);
        serviceIntent.putExtra("serverUrl", serverURL);
        serviceIntent.putExtra("deviceID", MessageHelper.DEVICE_ID);
        serviceIntent.putExtra("messenger", new Messenger(ChatFragment.SendMsgHandler));
        mChatItemListView.get().getContext().startService(serviceIntent);
        new MarkCurrentInputUsecaseImpl(mSaveLocalHistoryView.get().getContext(), input).execute();
    }

    public void markAndSendCurrentChatItem(ChatItem chatItem, boolean local) {
        if (mChatItemListView.get() == null)
            return;
//        new SendCommandAsyncTask(mChatItemListView.get().getContext(), chatItem, local).execute();
        Context context = mChatItemListView.get().getContext();
        String message;
        String serverURL;
        if (local) {
            message = MessageHelper.getFormatLocalMessage(chatItem.getContent());
            serverURL = MessageHelper.getLocalServerURL();
        } else {
            message = MessageHelper.getFormatMessage(chatItem.getContent());
            serverURL = MessageHelper.getServerURL(message);
        }
        Intent serviceIntent = new Intent(context, SendMsgIntentService.class);
        serviceIntent.putExtra("local", local);
        serviceIntent.putExtra("update", chatItem);
        serviceIntent.putExtra("cmdString", message);
        serviceIntent.putExtra("cmd", chatItem.getContent());
        serviceIntent.putExtra("serverUrl", serverURL);
        serviceIntent.putExtra("deviceID", MessageHelper.DEVICE_ID);
        serviceIntent.putExtra("messenger", new Messenger(ChatFragment.SendMsgHandler));
        mChatItemListView.get().getContext().startService(serviceIntent);
        new MarkCurrentInputUsecaseImpl(mSaveLocalHistoryView.get().getContext(), chatItem.getContent()).execute();
    }

    public void saveSaveLocalHistory() {
        new SaveAutoCompleteLocalHistoryUsecaseImpl(mSaveLocalHistoryView.get().getContext()).execute();
    }

    public void resetDatas(Context context) {
        List<ChatItem> chatItems = DBHelper.loadLatest(context, Constants.CHATITEM_LOAD_LIMIT);
        if (chatItems != null) {
            Collections.reverse(chatItems); // reverse descend items
            mChatItemListView.get().onResetDatas(chatItems);
        }
    }

    public void showNextCmdSuggestion(List<AutoCompleteItem> results, AutoCompleteItem item) {
        for (int i = 0; i < results.size() - 1; i++) {
            if (results.get(i).equals(item)) {
                item = results.get(i + 1);
                break;
            }
        }
        if (mChatSuggestionView.get() != null) {
            mChatSuggestionView.get().onShowSuggestion(item);
        }
    }

    public void showPreCmdSuggestion(List<AutoCompleteItem> results, AutoCompleteItem item) {
        for (int i = 1; i < results.size(); i++) {
            if (results.get(i).equals(item)) {
                item = results.get(i - 1);
                break;
            }
        }
        if (mChatSuggestionView.get() != null) {
            mChatSuggestionView.get().onShowSuggestion(item);
        }
    }

    @Override
    public void start() {
        BusProvider.getRestBusInstance().register(this);
    }

    @Override
    public void stop() {
        BusProvider.getRestBusInstance().unregister(this);
    }

    @Subscribe
    public void onSaveAutoCompleteLocalHistoryItems(DSaveAutoCompleteLocalHistoryEvent event) {
        if (event.getReturnCode() == DSaveAutoCompleteLocalHistoryEvent.SUCCESS)
            this.mSaveLocalHistoryView.get().onSaveLocalHistoryFinish(true);
        else
            this.mSaveLocalHistoryView.get().onSaveLocalHistoryFinish(false);
    }

    @Subscribe
    public void onShowCmdSuggestionEvent(DShowCmdSuggestionEvent event) {
        if (mChatSuggestionView.get() != null) {
            mChatSuggestionView.get().onShowSuggestion(event.getItem());
        }
    }

//    @Subscribe
//    public void onGetAutoCompleteItems(DShowAutoCompleteItemEvent event) {
//        if (mChatSuggestionView.get() != null) {
//            mChatSuggestionView.get().onGetAutoCompleteItems(event.getResultList());
//        }
//    }
}
