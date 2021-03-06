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

package my.home.model.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "MESSAGE_ITEM".
 */
public class MessageItem {

    private Long id;
    private int type;
    private int state;
    /**
     * Not-null value.
     */
    private String title;
    /**
     * Not-null value.
     */
    private String content;
    /**
     * Not-null value.
     */
    private java.util.Date date;

    public MessageItem() {
    }

    public MessageItem(Long id) {
        this.id = id;
    }

    public MessageItem(Long id, int type, int state, String title, String content, java.util.Date date) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * Not-null value.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Not-null value.
     */
    public String getContent() {
        return content;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Not-null value.
     */
    public java.util.Date getDate() {
        return date;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setDate(java.util.Date date) {
        this.date = date;
    }

}
