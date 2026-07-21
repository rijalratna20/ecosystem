package com.primus.cli.command;

import java.util.List;

/**
 * CLI command for application registration and metadata management.
 *
 * <p>Sub-commands:
 * <pre>
 *   primus app register --file metadata.yaml
 *   primus app list
 *   primus app show --id &lt;appId&gt;
 * </pre>
 */
public class AppCommand implements Command {

    @Override
    public String name() { return "app"; }

    @Override
    public String description() { return "Manage Primus application registrations"; }

    @Override
    public int execute(List<String> args) {
        if (args.isEmpty()) {
            printHelp();
            return 1;
        }
        String sub = args.get(0);
        List<String> rest = args.subList(1, args.size());
        return switch (sub) {
            case "register" -> register(rest);
            case "list"     -> list();
            case "show"     -> show(rest);
            default -> {
                System.err.println("Unknown sub-command: " + sub);
                printHelp();
                yield 1;
            }
        };
    }

    private int register(List<String> args) {
        String file = flagValue(args, "--file");
        if (file == null) { System.err.println("--file is required"); return 1; }
        System.out.println("Registering application from: " + file);
        // Real impl would POST to /api/v1/applications
        return 0;
    }

    private int list() {
        System.out.println("Listing registered applications...");
        // Real impl would GET /api/v1/applications
        return 0;
    }

    private int show(List<String> args) {
        String id = flagValue(args, "--id");
        if (id == null) { System.err.println("--id is required"); return 1; }
        System.out.println("Showing application: " + id);
        return 0;
    }

    private void printHelp() {
        System.out.println("Usage: primus app <register|list|show> [options]");
    }

    static String flagValue(List<String> args, String flag) {
        for (int i = 0; i < args.size() - 1; i++) {
            if (flag.equals(args.get(i))) return args.get(i + 1);
        }
        return null;
    }
}
