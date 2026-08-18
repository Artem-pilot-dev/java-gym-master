package ru.yandex.practicum.gym;

public class CounterOfTrainings implements Comparable<CounterOfTrainings> {
    private final Coach coach;
    private final int count;

    public CounterOfTrainings(Coach coach, int count) {
        this.coach = coach;
        this.count = count;
    }

    public Coach getCoach() {
        return coach;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(CounterOfTrainings o) {
        return o.count - this.count;
    }
}
