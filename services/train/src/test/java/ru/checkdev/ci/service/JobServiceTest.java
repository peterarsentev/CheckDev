package ru.checkdev.ci.service;

import org.junit.Ignore;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.io.File;
import java.text.ParseException;
import java.util.Properties;

/**

 *
 * @author Petr Arsentev (parsentev@yandex.ru)
 * @version $Id$
 * @since 0.1
 */
@Ignore
public class JobServiceTest {

    public static final class Job4j implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("test");
        }
    }

    @Test
    public void whenCreateDicThenPathShouldExic() {
        File file = new File("C:\\tmp\\students\\\\job4j\\");
        if (file.mkdirs()) {
            System.out.println(String.format("Folder %s is created", file.getAbsoluteFile()));
        } else {
            System.out.println(String.format("Folder %s could not created", file.getAbsoluteFile()));
        }
    }

    @Test
    public void whenStartupThenLoadAllJob() throws SchedulerException, ParseException, InterruptedException {
        final StdSchedulerFactory factory = new StdSchedulerFactory();
        Properties properties = new Properties();
        properties.setProperty("org.quartz.threadPool.threadCount", "1");
        factory.initialize(properties);
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();

        JobDetailImpl job = new JobDetailImpl();
        job.setName("job4j_test");
        job.setJobClass(Job4j.class);
        JobDataMap dataMap = new JobDataMap();
        job.setJobDataMap(dataMap);

        CronTriggerImpl trigger = new CronTriggerImpl();
        trigger.setName("trigger ");
        trigger.setCronExpression("0/5 * * * * ?");
        scheduler.scheduleJob(job, trigger);
        Thread.sleep(5000);
        factory.getScheduler().deleteJob(new JobKey("job4j_test"));
        Thread.sleep(60000);
    }
}