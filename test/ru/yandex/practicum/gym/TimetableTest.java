package ru.yandex.practicum.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TimetableTest {

    private Timetable timetable;
    private Coach coach1;
    private Coach coach2;
    private Group groupChild;
    private Group groupAdult;
    private static final int CHILD_SESSION_DURATION_MINUTES = 60;
    private static final int ADULT_SESSION_DURATION_MINUTES = 90;
    private static final int HOUR_9 = 9;
    private static final int HOUR_10 = 10;
    private static final int HOUR_12 = 12;
    private static final int HOUR_14 = 14;
    private static final int HOUR_13 = 13;
    private static final int HOUR_18 = 18;
    private static final int HOUR_20 = 20;
    private static final int MINUTE_ZERO = 0;

    @BeforeEach
    void setUp() {
        timetable = new Timetable();
        coach1 = new Coach("Васильев", "Николай", "Сергеевич");
        coach2 = new Coach("Саламатов", "Игорь", "Петрович");
        groupChild = new Group("Акробатика для детей", Age.CHILD, CHILD_SESSION_DURATION_MINUTES);
        groupAdult = new Group("Акробатика для взрослых", Age.ADULT, ADULT_SESSION_DURATION_MINUTES);
    }

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        TrainingSession singleTrainingSession = new TrainingSession(groupChild, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));

        timetable.addNewTrainingSession(singleTrainingSession);

        Collection<TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assertEquals(1, mondaySessions.size(), "Должна вернуться ровно 1 тренировка");
        assertTrue(mondaySessions.contains(singleTrainingSession), "Список должен содержать добавленную тренировку");

        // Проверить, что за вторник не вернулось занятий
        Collection<TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        assertTrue(tuesdaySessions.isEmpty(), "За вторник расписание должно быть пустым");
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach1,
                DayOfWeek.THURSDAY, new TimeOfDay(HOUR_20, MINUTE_ZERO));
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach1,
                DayOfWeek.THURSDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach1,
                DayOfWeek.SATURDAY, new TimeOfDay(HOUR_10, MINUTE_ZERO));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);
        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        assertEquals(1, timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY).size(),"В понедельник 1 занятие");
        assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty(),"Во вторник нет занятий");

        List<TrainingSession> thursdaySessions = (List<TrainingSession>) timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);

        assertEquals(2, thursdaySessions.size(), "В четверг должно быть 2 тренировки");
        assertEquals(thursdayChildTrainingSession, thursdaySessions.get(0), "Первая тренировка в 13:00");
        assertEquals(thursdayAdultTrainingSession, thursdaySessions.get(1), "Вторая тренировка в 20:00");
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        TrainingSession singleTrainingSession = new TrainingSession(groupChild, coach1,
                DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));

        timetable.addNewTrainingSession(singleTrainingSession);

        List<TrainingSession> sessionAt13 = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));
        assertEquals(1, sessionAt13.size(),"В понедельник 1 занятие");
        assertTrue(sessionAt13.contains(singleTrainingSession));

        List<TrainingSession> sessionAt14 = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(HOUR_14, MINUTE_ZERO));
        assertTrue(sessionAt14.isEmpty(), "В 14:00 тренировок быть не должно");
    }

    @Test
    void testGetTrainingSessionsForDayAndTimeTheSameTime() {
        TrainingSession session1 = new TrainingSession(groupChild, coach1, DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));
        TrainingSession session2 = new TrainingSession(groupAdult, coach2, DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));

        timetable.addNewTrainingSession(session1);
        timetable.addNewTrainingSession(session2);

        List<TrainingSession> result = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(HOUR_13, MINUTE_ZERO));
        assertEquals(2, result.size(), "В 13:00 проходит 2 занятия");
        assertTrue(result.contains(session1));
        assertTrue(result.contains(session2));
    }

    @Test
    void testGetTrainingSessionsForDaySortingWithMultipleSessionsAtSameTime() {
        TrainingSession evening = new TrainingSession(groupAdult, coach1, DayOfWeek.FRIDAY, new TimeOfDay(HOUR_18, MINUTE_ZERO));
        TrainingSession morning1 = new TrainingSession(groupChild, coach1, DayOfWeek.FRIDAY, new TimeOfDay(HOUR_9, MINUTE_ZERO));
        TrainingSession morning2 = new TrainingSession(groupAdult, coach2, DayOfWeek.FRIDAY, new TimeOfDay(HOUR_9, MINUTE_ZERO));

        timetable.addNewTrainingSession(evening);
        timetable.addNewTrainingSession(morning1);
        timetable.addNewTrainingSession(morning2);

        List<TrainingSession> fridaySessions = (List<TrainingSession>) timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY);
        assertEquals(3, fridaySessions.size());
        // Первые две тренировки должны идти на 9:00, последняя — на 18:00
        assertEquals(new TimeOfDay(HOUR_9, 0), fridaySessions.get(0).getTimeOfDay(),"Тренировка в 9");
        assertEquals(new TimeOfDay(HOUR_9, 0), fridaySessions.get(1).getTimeOfDay(),"Тренировка в 9");
        assertEquals(new TimeOfDay(HOUR_18, 0), fridaySessions.get(2).getTimeOfDay(),"Тренировка в 18");
    }

    @Test
    void testGetTrainingSessionsForNonExistentDayReturnsEmpty() {
        Collection<TrainingSession> sundaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.SUNDAY);
        assertNotNull(sundaySessions);
        assertTrue(sundaySessions.isEmpty(), "Должна вернуться пустая коллекция, а не null");
    }

    @Test
    void testGetCountByCoachesOrderedByCountDesc() {
        // Проверка правильности сортировки тренеров по убыванию количества занятий
        TrainingSession s1 = new TrainingSession(groupChild, coach1, DayOfWeek.MONDAY, new TimeOfDay(HOUR_10, MINUTE_ZERO));
        TrainingSession s2 = new TrainingSession(groupChild, coach1, DayOfWeek.WEDNESDAY, new TimeOfDay(HOUR_10, MINUTE_ZERO));
        TrainingSession s3 = new TrainingSession(groupAdult, coach2, DayOfWeek.TUESDAY, new TimeOfDay(HOUR_12, MINUTE_ZERO));

        timetable.addNewTrainingSession(s1);
        timetable.addNewTrainingSession(s2); // У coach1 — 2 тренировки
        timetable.addNewTrainingSession(s3); // У coach2 — 1 тренировка

        List<CounterOfTrainings> result = timetable.getCountByCoaches();
        assertEquals(2, result.size());

        // Первым должен идти coach1 (2 занятия), вторым coach2 (1 занятие)
        assertEquals(coach1, result.get(0).getCoach());
        assertEquals(2, result.get(0).getCount(),"Тренер 1 - 2 занятия");

        assertEquals(coach2, result.get(1).getCoach());
        assertEquals(1, result.get(1).getCount(),"Тренер 2 - 1 занятие");
    }

    @Test
    void testGetCountByCoachesEmptyTimetable() {
        List<CounterOfTrainings> result = timetable.getCountByCoaches();
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Пустое расписание должно быть пустым");
    }

    @Test
    void testGetCountByCoachesWithSameCount() {
        TrainingSession s1 = new TrainingSession(groupChild, coach1, DayOfWeek.MONDAY, new TimeOfDay(HOUR_10, MINUTE_ZERO));
        TrainingSession s2 = new TrainingSession(groupAdult, coach2, DayOfWeek.TUESDAY, new TimeOfDay(HOUR_12, MINUTE_ZERO));

        timetable.addNewTrainingSession(s1);
        timetable.addNewTrainingSession(s2);

        List<CounterOfTrainings> result = timetable.getCountByCoaches();
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getCount());
        assertEquals(1, result.get(1).getCount());
    }
}

