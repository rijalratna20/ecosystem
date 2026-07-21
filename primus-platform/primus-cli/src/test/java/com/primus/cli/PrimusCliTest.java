package com.primus.cli;

import com.primus.cli.runner.PrimusCli;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrimusCliTest {

    private PrimusCli cli;

    @BeforeEach
    void setUp() {
        cli = new PrimusCli();
    }

    @Test
    void no_args_returns_nonzero() {
        assertEquals(1, cli.run(new String[]{}));
    }

    @Test
    void help_returns_zero() {
        assertEquals(0, cli.run(new String[]{"help"}));
        assertEquals(0, cli.run(new String[]{"--help"}));
    }

    @Test
    void unknown_command_returns_nonzero() {
        assertEquals(1, cli.run(new String[]{"foobar"}));
    }

    @Test
    void app_register_without_file_returns_nonzero() {
        assertEquals(1, cli.run(new String[]{"app", "register"}));
    }

    @Test
    void app_register_with_file_returns_zero() {
        assertEquals(0, cli.run(new String[]{"app", "register", "--file", "app.yaml"}));
    }

    @Test
    void export_run_without_app_returns_nonzero() {
        assertEquals(1, cli.run(new String[]{"export", "run"}));
    }

    @Test
    void export_run_with_app_returns_zero() {
        assertEquals(0, cli.run(new String[]{"export", "run", "--app", "LoanService", "--date", "2024-01-01"}));
    }

    @Test
    void app_list_returns_zero() {
        assertEquals(0, cli.run(new String[]{"app", "list"}));
    }

    @Test
    void export_list_returns_zero() {
        assertEquals(0, cli.run(new String[]{"export", "list", "--status", "COMPLETED"}));
    }
}
