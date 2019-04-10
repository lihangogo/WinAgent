package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mybatis.OnlineBean;
import com.mybatis.OnlineService;
import com.mybatis.OnlineTempBean;
import com.mybatis.OnlineTempService;

/**
 * 检查在线状态
 * @author 李瀚
 *
 */
public class CheckOnlineStatus {
	 
	/**
	 * 实时检查
	 */
	public static void check() {
		OnlineService onlineService=new OnlineService();
		ArrayList<OnlineBean> list=onlineService.getOnlineList();
		copyOnline(list);
		/*
		for(OnlineBean ob:list) {
			if(!ping2(ob.getIpAddress_192(),3)) {   //客户端不再连接			
				//int sum=computeFee(ob.getStartTimeStamp());
				//updateAccount(ob.getUid(), sum);     //更新账户
				onlineService.deleteOnlineRecord(ob.getUid());  //删除在线记录
			}
		}*/
	}
	
	/**
	 * 保存一天内登录过的用户 
	 * @param list
	 */
	private static void copyOnline(ArrayList<OnlineBean> list) {
		OnlineTempService onlineTempService=new OnlineTempService();
		ArrayList<OnlineTempBean> listTemp=onlineTempService.getOnlineTempList();
		boolean tag=true;
		for(OnlineBean ob:list) {
			tag=true;
			for(OnlineTempBean otb:listTemp) {
				if(ob.getUid().intValue()==otb.getUid().intValue()) {
					tag=false;
					break;
				}
			}
			if(tag) {   //今日未登陆过
				onlineTempService.addOnlineTemp(ob);
			}
		}
	}
	
	/**
	 * 更新账户余额
	 * @param uid
	 * @param sum
	 */
	/*
	private static void updateAccount(int uid,int sum) {
		AccountService accountService=new AccountService();
		AccountBean goal=accountService.selectAccountByUID(uid);
		goal.setBalance(goal.getBalance()-sum);
		accountService.updateAccount(goal);
	}*/
	
	/**
     * 检查是否能够连接到服务器
     * 降低了相比ping, ping2仅要求收到第一个包即可
     */
    public static boolean ping2(String ipAddress, int pingTimes) {
    	BufferedReader in = null;  
        Runtime r = Runtime.getRuntime();
        String pingCommand = "ping " + ipAddress + " -c " + pingTimes; 
        // 执行命令并获取输出  
        try {
            Process p = r.exec(pingCommand);   
            if (p == null) {    
                return false;   
            }
            // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数  
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;   
            while ((line = in.readLine()) != null) {          
            	// 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真  
            	if(getCheckResult(ipAddress, line) == 1) {
            		return true;
            	}
            }   
            return false;  
        } catch (Exception ex) {  
        	// 出现异常则返回假  
            return false;  
        } finally {   
            try {    
                in.close();   
            } catch (IOException e) {}  
        }
    }
    
    /**
     * 获得检查结果，连通或不连通
     * @param ip
     * @param line
     * @return
     */
    private static int getCheckResult(String ip, String line) {  
    	//正则匹配，包含对应一项即可
        Pattern pattern = Pattern.compile("(TTL=(\\d+))(\\s+)(time=((\\d+)(.*)(\\d+)(\\s+)ms))", Pattern.CASE_INSENSITIVE);  
        Matcher matcher = pattern.matcher(line);  
        if(matcher.find()) {
        	//System.out.println("detIP=" + ip + " delay=" + matcher.group(2) + "ms ttl=" + matcher.group(5));
        	return 1;
        }
        return 0; 
    }
    
}
