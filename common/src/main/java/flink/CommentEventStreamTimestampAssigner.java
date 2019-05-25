package flink;


import com.dspa.project.model.CommentEventStream;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;


public class CommentEventStreamTimestampAssigner extends BoundedOutOfOrdernessTimestampExtractor<CommentEventStream> {
    public CommentEventStreamTimestampAssigner(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(CommentEventStream commentEventStream) {
        return commentEventStream.getSentAt().getTime();
    }



//    @Override
//    public long extractTimestamp(CommentEventStream element, long previousElementTimestamp) {
//        return element.getSentAt().getTime();
//    }
//
//    @Nullable
//    @Override
//    public Watermark checkAndGetNextWatermark(CommentEventStream lastElement, long extractedTimestamp) {
//        return new Watermark(extractedTimestamp - 300000); //TODO: this has to be equal to the random amount of delay a message can have
//    }
}
