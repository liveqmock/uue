package com.zihexin.business_interface.common.socket.connect;

import java.util.Date;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zihexin.business_interface.common.DateUtils;
import com.zihexin.business_interface.common.Utils;
import com.zihexin.business_interface.dao.ReadPropertiesDao;
import com.zihexin.business_interface.domain.Command;
import com.zihexin.business_interface.domain.Message;

/**
 * �����е�socket���������
 * User: Administrator
 *
 */
public class BankConnectorSupport extends AbcConnectorSupport {
    private final static Logger logger = LoggerFactory.getLogger(BankConnectorSupport.class);
    private static NioSocketConnector connector;
    private final static String BODY_TYPE = "2";//�������;1:�����ģ�2����Ӧ����
    
    private ReadPropertiesDao readPropertiesDao;
    
     public BankConnectorSupport(DataAnalyze dataAnalyze) {
        super(dataAnalyze);
    }

    /**
     * ͬ�������ӣ��õ�����������ݣ���ɻỰ��ر����ӣ�
     * @param
     * @return
     */
    /**
     * @param request
     * @param srcWaterNum
     * @return
     */
    public  String requestProcessor(byte [] request,String srcWaterNum){
    	
         Date requestDate = new Date();//����ʱ��
         Command _command = new Command();
         Message reciveData = new Message();
        IoSession session  = null;
        byte[]  xmlResponse = null;
        String xml = null;
        synchronized (this) {
            if(connector == null){
                connector = (NioSocketConnector) runInit();
            }
        }

       try {
           ConnectFuture connectFuture = connector.connect();
           connectFuture.awaitUninterruptibly();
           session = connectFuture.getSession();
           session.getConfig().setUseReadOperation(true);
           //session.getConfig().setReadBufferSize(4096) ;
           session.getFilterChain().remove("protocolCodeFilter") ;
           session.getFilterChain().addLast("protocolCodeFilter",  new ProtocolCodecFilter(null, new SpdbDecode())) ;
           logger.info("�������е�ʱ��Ϊ��"+DateUtils.now());
           session.write(request).awaitUninterruptibly();
           ReadFuture readFuture = session.read();
           readFuture.awaitUninterruptibly();
           
           xmlResponse = (byte[]) readFuture.getMessage();
           if(xmlResponse != null){
               xml = new String(xmlResponse, Utils.CHARSET_FORNAME);
           }
	        logger.info("������Ӧ��ʱ��Ϊ��"+DateUtils.now());
           return xml;
      } catch (Exception e){
    	  e.printStackTrace();
          logger.error("�������������쳣��");
          e.printStackTrace();
      }finally{
          if(session != null)  session.close(true);
           //if(connector != null) connector.dispose();
          
          	//��¼������ˮ������Ӧ������־
	        logger.info("��ʼ���ɽ�����ˮ������Ӧ������־");
	        reciveData.setMsg(xml);
	        _command.setMessage(reciveData);
	        _command.setRequestDt(requestDate);
	        _command.setBodyType(BODY_TYPE);
	        _command.setCommandSrc("sys009");
	        _command.setSrc_water_num(srcWaterNum);
	        boolean logSuccess = readPropertiesDao.saveTransLog(_command);
	        logger.info(reciveData.getLogId() + "������־��д�����ݿ�" + (logSuccess == true ? "�ɹ�" : "ʧ��"));
	        logger.info(reciveData.getLogId() + "�������ɽ�����ˮ���ͱ�����־");
      }
      return "";
    }

	public ReadPropertiesDao getReadPropertiesDao() {
		return readPropertiesDao;
	}

	public void setReadPropertiesDao(ReadPropertiesDao readPropertiesDao) {
		this.readPropertiesDao = readPropertiesDao;
	}
    
    
}