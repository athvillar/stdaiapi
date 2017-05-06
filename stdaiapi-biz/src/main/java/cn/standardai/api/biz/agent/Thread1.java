package cn.standardai.api.biz.agent;

import cn.standardai.api.core.util.DateUtil;
import cn.standardai.api.dao.JDDao;

class Thread1 extends Thread{  
    private JDDao dao;  
    String[] data;
    public Thread1(JDDao dao, String[] data) {  
       this.dao=dao;  
       this.data = data;
    }  
    public void run() {  
    	//synchronized(this.dao){

    		dao.insertAction(data[0], data[1], DateUtil.parse(data[2], DateUtil.YYYY_MM_DD),
    				data[3], data[4], data[5], data[6],
    				data[7], data[8], DateUtil.parse(data[9], DateUtil.YYYY_MM_DD),
    				data[10], data[11], data[12], data[13],
    				data[14], data[15], DateUtil.parse(data[16], DateUtil.YYYY_MM_DD),
    				data[17], data[18], data[19], data[20]);
    	//}
    }  
}  
