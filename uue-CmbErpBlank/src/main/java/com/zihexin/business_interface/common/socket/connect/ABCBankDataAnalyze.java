package com.zihexin.business_interface.common.socket.connect;

import com.zihexin.business_interface.common.Utils;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解码策略
 */
public class ABCBankDataAnalyze  implements DataAnalyze {

    private final static Logger logger = LoggerFactory.getLogger(AbcConnectorSupport.class);

    /**
     * 解码策略
     * @param buffer
     * @return
     */
    public byte[] decode(IoBuffer buffer) {
        logger.info("解码开始...");

        //读取一个字节  暂时没有用到
        //byte lenByte = buffer.get();
        //buffer.capacity() ;
//        IoBuffer _buffer = buffer.allocate(4005).setAutoExpand(true);
//        _buffer.put(buffer.array()) ;
//        
//        buffer = _buffer ;
        
        byte[] lenBytes = new byte[8];
        buffer.get(lenBytes);
        int len = 0;
        byte[] content = null;
        try {
            String str = new String(lenBytes, Utils.CHARSET_FORNAME);
            len = Integer.valueOf(new String(str.trim().getBytes() , Utils.CHARSET_FORNAME));
            content = new byte[len];
            buffer.get(content);
            logger.info("解码完成，收到到响应报文的完整数据,报文头：" + new String(lenBytes,  Utils.CHARSET_FORNAME));
            logger.info("解码完成，收到到响应报文的完整数据,报文体：" + new String(content,  Utils.CHARSET_FORNAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 编码策略
     * @param message
     * @return
     */
    public IoBuffer encode(Object message) {
        IoBuffer buffer = IoBuffer.allocate(8).setAutoExpand(true);
        byte[] msg = (byte[]) message;
        try {
            logger.info("发送到银行的完整报文:"+new String(msg,Utils.CHARSET_FORNAME));
            buffer.put(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.flip();
        //logger.info("编码完成，发送到的数据："+buffer.getHexDump());
        return buffer;
    }
}


