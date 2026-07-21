package com.primus.cli.command;

import java.util.List;

/**
 * CLI command for export management.
 *
 * <p>Sub-commands:
 * <pre>
 *   primus export run --app LoanService --date 2024-01-01
 *   primus exports list --status COMPLETED --format json
 * </pre>
 */
public class ExportCommand implements Command {

    @Override
    public String name() { return "export"; }

    @Override
    public String description() { return "Trigger and query data exports"; }

    @Override
    public int execute(List<String> args) {
        if (args.isEmpty()) { printHelp(); return 1; }
        String sub = args.get(0);
        List<String> rest = args.subList(1, args.size());
        return switch (sub) {
            case "run"  -> run(rest);
            case "list" -> list(rest);
            default -> { System.err.println("Unknown sub-command: " + sub); printHelp(); yield 1; }
        };
    }

    private int run(List<String> args) {
        String app = AppCommand.flagValue(args, "--app");
        String date = AppCommand.flagValue(args, "--date");
        if (app == null) { System.err.println("--app is required"); return 1; }
        System.out.printf("Triggering export for app=%s date=%s%n", app, date);
        // Real impl: POST /api/v1/exports
        return 0;
    }

    private int list(List<String> args) {
        String status = AppCommand.flagValue(args, "--status");
        String format = AppCommand.flagValue(args, "--format");
        System.out.printf("Listing exports status=%s format=%s%n", status, format);
        // Real impl: GET /api/v1/exports?status=...
        return 0;
    }

    private void printHelp() {
        System.out.println("Usage: primus export <run|list> [options]");
    }
}
