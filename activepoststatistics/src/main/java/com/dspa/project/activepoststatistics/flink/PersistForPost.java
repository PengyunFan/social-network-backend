package com.dspa.project.activepoststatistics.flink;

import com.dspa.project.activepoststatistics.SpringBeansUtil;
import com.dspa.project.activepoststatistics.repo.CommentAndReplyRepository;
import com.dspa.project.activepoststatistics.repo.PostAndCommentRepository;
import com.dspa.project.activepoststatistics.repo.PostAndDateRepository;
import com.dspa.project.model.PostAndDate;
import com.dspa.project.model.PostEventStream;
import org.apache.flink.api.common.functions.MapFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistForPost implements MapFunction<PostEventStream,PostEventStream> {
    @Autowired
    public transient PostAndDateRepository postAndDateRepository;
    @Autowired
    public transient PostAndCommentRepository postAndCommentRepository;
    @Autowired
    public transient CommentAndReplyRepository commentAndReplyRepository;

    @Override
    public PostEventStream map(PostEventStream postEventStream) throws Exception {
        PostAndDate postAndDate = new PostAndDate();

        if(this.postAndDateRepository==null){
            postAndDateRepository = SpringBeansUtil.getBean(PostAndDateRepository.class);
        }
        if(this.postAndCommentRepository==null){
            postAndCommentRepository = SpringBeansUtil.getBean(PostAndCommentRepository.class);
        }
        if(this.commentAndReplyRepository==null){
            commentAndReplyRepository = SpringBeansUtil.getBean(CommentAndReplyRepository.class);
        }

        if(postAndDateRepository!=null){
            postAndDate.setId(postEventStream.getId());
            postAndDate.setLastUpdate(postEventStream.getSentAt());
            postAndDateRepository.save(postAndDate);
        }

        return postEventStream;
    }
}