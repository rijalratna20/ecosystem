package com.primus.cli.runner;

import com.primus.cli.command.AppCommand;
import com.primus.cli.command.Command;
import com.primus.cli.command.ExportCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for the Primus CLI tool.
 *
 * <p>Usage:
 * <pre>
 *   java -jar primus-cli.jar &lt;command&gt; [sub-command] [options]
 * </pre>
 *
 * <p>Commands are registered at construction time; new commands can be added
 * by calling {@link #register(Command)}.
 */
public class PrimusCli {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public PrimusCli() {
        register(new AppCommand());
        register(new ExportCommand());
    }

    public void register(Command command) {
        commands.put(command.name(), command);
    }

    /**
     * Parse and dispatch the provided argument array.
     *
     * @param args raw CLI arguments
     * @return exit code (0 = success)
     */
    public int run(String[] args) {
        if (args == null || args.length == 0) {
            printHelp();
            return 1;
        }
        String verb = args[0];
        if ("--help".equals(verb) || "-h".equals(verb) || "help".equals(verb)) {
            printHelp();
            return 0;
        }
        Command cmd = commands.get(verb);
        if (cmd == null) {
            System.err.println("Unknown command: " + verb);
            printHelp();
            return 1;
        }
        List<String> rest = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
        return cmd.execute(rest);
    }

    private void printHelp() {
        System.out.println("Usage: primus <command> [sub-command] [options]");
        System.out.println();
        System.out.println("Commands:");
        commands.forEach((name, cmd) ->
                System.out.printf("  %-15s %s%n", name, cmd.description()));
        System.out.println();
        System.out.println("Run 'primus <command> --help' for command-specific help.");
    }

    public static void main(String[] args) {
        System.exit(new PrimusCli().run(args));
    }
}
