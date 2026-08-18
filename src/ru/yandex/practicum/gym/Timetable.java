package ru.yandex.practicum.gym;

import java.util.*;

public class Timetable {

    private final Map<DayOfWeek, Map<TimeOfDay, List<TrainingSession>>> timetable = new HashMap<>();

    public void addNewTrainingSession(TrainingSession trainingSession) {
        DayOfWeek day = trainingSession.getDayOfWeek();
        TimeOfDay timeOfDay = trainingSession.getTimeOfDay();
        Map<TimeOfDay, List<TrainingSession>> dailySessions = timetable.getOrDefault(day, new TreeMap<>());
        timetable.put(day, dailySessions);
        List<TrainingSession> sessionAtTime = dailySessions.getOrDefault(timeOfDay, new ArrayList<>());
        dailySessions.put(timeOfDay, sessionAtTime);
        sessionAtTime.add(trainingSession);
    }

    public Collection<TrainingSession> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        TreeMap<TimeOfDay, List<TrainingSession>> dailySessions =
                (TreeMap<TimeOfDay, List<TrainingSession>>) timetable.get(dayOfWeek);
        if (dailySessions == null) {
            return Collections.emptyList();
        }

        List<TrainingSession> orderedSessions = new ArrayList<>();
        for (TimeOfDay time : dailySessions.navigableKeySet()) {
            orderedSessions.addAll(dailySessions.get(time));
        }
        return orderedSessions;
    }

    public List<TrainingSession> getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        Map<TimeOfDay, List<TrainingSession>> dailySessions = timetable.get(dayOfWeek);
        if (dailySessions == null) {
            return Collections.emptyList();
        }
        return dailySessions.getOrDefault(timeOfDay, Collections.emptyList());
    }


    public List<CounterOfTrainings> getCountByCoaches() {
        Map<Coach, Integer> counts = new HashMap<>();

        for (Map<TimeOfDay, List<TrainingSession>> dailySessions : timetable.values()) {
            for (List<TrainingSession> sessions : dailySessions.values()) {
                for (TrainingSession session : sessions) {
                    Coach coach = session.getCoach();
                    if (coach != null) {
                        counts.put(coach, counts.getOrDefault(coach, 0) + 1);
                    }
                }
            }
        }
        List<CounterOfTrainings> resultList = new ArrayList<>();
        for (Map.Entry<Coach, Integer> entry : counts.entrySet()) {
            resultList.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));
        }
        Collections.sort(resultList);

        return resultList;
    }
}

