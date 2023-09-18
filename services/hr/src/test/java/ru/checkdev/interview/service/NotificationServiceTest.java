package ru.checkdev.interview.service;

import org.junit.Ignore;
import org.junit.Test;
import ru.checkdev.interview.domain.*;
import ru.checkdev.interview.repository.ITaskRepository;
import ru.checkdev.interview.repository.TrackRepository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

/**

 *
 * @author Petr Arsentev (parsentev@yandex.ru)
 * @version $Id$
 * @since 0.1
 */
@Ignore
public class NotificationServiceTest {
    @Test
    public void whenTwoCall() {
        assertThat(
                NotificationService.startOfDay(Calendar.getInstance()),
                is(NotificationService.startOfDay(Calendar.getInstance()))
        );
    }

    @Test
    public void whenHasInterviewThenSend() throws Exception {
        InterviewService interviews = mock(InterviewService.class);
        TrackRepository tracks = mock(TrackRepository.class);
        ITaskRepository itasks = mock(ITaskRepository.class);
        Interview interview = new Interview(123,
                "1c9ec92496de9d6d5e612987e371f2d3ff6ab37ea7ad07b8ac898ec98ff9b9a0732535a5693ed49d", Calendar.getInstance(), Calendar.getInstance(),
                Interview.Result.PROCESS,
                new Vacancy(6, "Test Vacancy Report",
                        "remote", "300$/h", Calendar.getInstance(), true, "short",
                        "job4j", "3", Calendar.getInstance(), "f6fe9136ef0ec0938b19261850be2a9987c9884d839ce17a1fcad75990438129b49061b5b8f5326e", 1, false)
        );

        NotificationService subscribe = new NotificationService(
                "http://localhost:9920", "96GcWB8a",
                "http://localhost:9900", "9dfs23vd2", null);
        subscribe.send(interview);
    }

    /**
     * test send mail
     */
    @Test
    public void testSendInviteMessageToMail() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(12);
        vacancy.setActive(false);
        vacancy.setLocation("RnD");
        vacancy.setCreated(Calendar.getInstance());
        vacancy.setDescription("TEST");
        vacancy.setCompany("TEST");
        vacancy.setExperience("1");
        vacancy.setName("TEST");
        NotificationService subscribe = new NotificationService(
        "http://localhost:9920", "96GcWB8a",
        "http://localhost:9900", "9dfs23vd2", null);
        Map<String, Object> keys = new HashMap<>();
        keys.put("subject", vacancy.getName());
        keys.put("body", vacancy.getDescription());
        Notify notify = new Notify("lotnikroman@ya.ru", keys, Notify.Type.RESULT_OF_INTERVIEW.name());
        subscribe.invite(notify);
    }
}