package com.ms.silverking.cloud.dht.net;

import java.nio.ByteBuffer;
import java.util.List;

import com.ms.silverking.cloud.dht.common.MessageType;
import com.ms.silverking.cloud.dht.net.protocol.KeyValueMessageFormat;
import com.ms.silverking.cloud.dht.net.protocol.PutUpdateMessageFormat;
import com.ms.silverking.cloud.dht.trace.TraceIDProvider;
import com.ms.silverking.id.UUIDBase;

public class ProtoPutUpdateMessageGroup extends ProtoKeyedMessageGroup {
  private static final int keyBufferAdditionalBytesPerKey = 0;
  private static final int optionsBufferSize = PutUpdateMessageFormat.optionBytesSize;

  public ProtoPutUpdateMessageGroup(UUIDBase uuid, long context, long version,
      List<MessageGroupKeyOrdinalEntry> destEntries, byte[] originator, byte storageState, int deadlineRelativeMillis,
      byte[] maybeTraceID) {
    super(TraceIDProvider.isValidTraceID(maybeTraceID) ? MessageType.PUT_UPDATE_TRACE : MessageType.PUT_UPDATE, uuid,
        context, ByteBuffer.allocate(optionsBufferSize), destEntries.size(), keyBufferAdditionalBytesPerKey, originator,
        deadlineRelativeMillis, ForwardingMode.DO_NOT_FORWARD, maybeTraceID);
    bufferList.add(optionsByteBuffer);
    optionsByteBuffer.putLong(version);
    optionsByteBuffer.put(storageState);
    for (MessageGroupKeyOrdinalEntry keyOrdinalEntry : destEntries) {
      addKey(keyOrdinalEntry);
      // FIXME - the orginal is unused since it's the same and stored as
      // storageState in the optionsByteBuffer
      // remove the per entry ordinal and remove the type from the list etc.
    }
  }

  public static ByteBuffer getOptionBuffer(MessageGroup mg) {
    int startIdx;

    // This protocol appends the optionBuffer to its baseClass(ProtoKeyedMessageGroup)'s bufferList
    startIdx = ProtoKeyedMessageGroup.getOptionsByteBufferBaseOffset(TraceIDProvider.hasTraceID(mg.getMessageType()));
    return mg.getBuffers()[startIdx];
  }

  public static long getPutVersion(MessageGroup mg) {
    //System.out.println(mg.getBuffers()[optionBufferIndex]);
    return getOptionBuffer(mg).getLong(PutUpdateMessageFormat.versionOffset);
  }

  // FIXME - NEED TO MAKE CONSISTENT WHETHER WE USE THE PROTO CLASSES OR THE FORMAT CLASSES
  // also need to decide where MetaDataUtil should live and how it should be used

  public static byte getStorageState(MessageGroup mg) {
    //System.out.println(mg.getBuffers()[optionBufferIndex]);
    //return mg.getBuffers()[optionBufferIndex].getLong(PutUpdateMessageFormat.versionOffset);
    return PutUpdateMessageFormat.getStorageState(getOptionBuffer(mg));
  }
}
