package com.dspa.project.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "comment_and_reply")
public class CommentAndReply {


    private int commentId;
    @Id
    private int replyId;

    public int getCommentId() {
        return commentId;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }
}
