package com.zyc.zdh.job;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zyc.zdh.dao.QuartzJobMapper;
import com.zyc.zdh.entity.QuartzJobInfo;
import com.zyc.zdh.service.ZdhLogsService;
import com.zyc.zdh.util.SpringContext;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;


@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MyJobBean extends QuartzJobBean implements Serializable {

	private static Logger logger = LoggerFactory.getLogger(MyJobBean.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -8509585011462529939L;

	public static final String TASK_ID = "task_id";
	
	@Autowired
	private QuartzJobMapper quartzJobMapper;

	public QuartzJobMapper getQuartzJobMapper() {
		return quartzJobMapper;
	}

	public void setQuartzJobMapper(QuartzJobMapper quartzJobMapper) {
		this.quartzJobMapper = quartzJobMapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
//			System.out.println(jobDataMap.get(TASK_ID)
//					+ "----执行-----"
//					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//							.format(new Date()));
			String taskId = jobDataMap.getString(TASK_ID);
			if (taskId == null || taskId.trim().equals("")) {
				throw new Exception("任务id为空");
			}
			// 记录当前时间更新任务最后执行时间
			Date currentTime = new Date();

			QuartzJobMapper quartzJobMapper2 = this.quartzJobMapper;
			QuartzJobInfo quartzJobInfo = new QuartzJobInfo();
			quartzJobInfo = quartzJobMapper2.selectByPrimaryKey(taskId);

			if(quartzJobInfo==null){
				logger.info("调度任务发现空的任务,任务id"+taskId);
			}
            //选择具体的job 执行引擎
			JobCommon.chooseJobBean(quartzJobInfo,false);



		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

}