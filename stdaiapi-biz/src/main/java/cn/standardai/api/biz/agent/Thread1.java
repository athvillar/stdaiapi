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
    				data[3], data[4], data[5], data[6]);
    	//}
    }  
}  
