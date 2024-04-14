package com.example.MyRadiostation.controllers;
import com.example.MyRadiostation.models.Album;
import com.example.MyRadiostation.models.Track;
import com.example.MyRadiostation.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.MyRadiostation.models.Schedule;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@EnableMethodSecurity
@Controller
@RequiredArgsConstructor
public class ScheduleController {
    @Autowired
    private final ScheduleRepository scheduleRepository;

    @GetMapping("/schedule")
    public String allSchedule(Model model ) {
            Iterable<Schedule> schedule = scheduleRepository.findAll();
            model.addAttribute("schedule", schedule);
            return "schedule";

    }

    @GetMapping("/schedule/{id}")
    public String getProgram(@PathVariable(value = "id") Long id, Model model) {
        Optional<Schedule> program = scheduleRepository.findById(id);
        ArrayList<Schedule> res = new ArrayList<>();
        program.ifPresent(res::add);
        model.addAttribute("program",res);
        return "program-info";
    }

    @GetMapping("/schedule-find/**")
    public String getProgramByDayofprogram(@RequestParam(name = "dayofprogram", required = false) LocalDate dayofprogram, Model model) {
        model.addAttribute("schedule",scheduleRepository.findByDayofprogram(dayofprogram));
        return "schedule-find";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/schedule/create")
    public String programCreate(Schedule program) {
        return "program-create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/schedule/create")
    public String programCreate(@RequestParam(required = false) String pname, @RequestParam(value = "dayofprogram",required = false) LocalDate dayofprogram, @RequestParam(value = "timestart",required = false)  String timestart, @RequestParam(value = "timeend",required = false)  String timeend) {
        scheduleRepository.save(new Schedule(pname,dayofprogram,LocalTime.parse(timestart),LocalTime.parse(timeend)));
        return "redirect:/schedule";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/schedule/{id}/delete")
    public String programDelete(@PathVariable(value="id") Long id) {
        Schedule program = scheduleRepository.findById(id).orElseThrow();
        scheduleRepository.delete(program);
        return "redirect:/schedule";
    }

}