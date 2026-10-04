package xyz.erupt.pm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import xyz.erupt.core.annotation.EruptScan;

/**
 * Project management application built on Erupt: projects, iterations, tasks, worklogs and risks,
 * every screen generated from annotations.
 *
 * @author YuePeng
 */
@EruptScan
@EntityScan
@SpringBootApplication
public class PmApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmApplication.class, args);
    }

}
