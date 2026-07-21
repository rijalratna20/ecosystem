package com.primus.cli.command;

import java.util.List;

/**
 * SPI for a CLI command (verb + sub-verb pattern).
 *
 * <p>Usage pattern:
 * <pre>
 *   primus &lt;command&gt; &lt;sub-command&gt; [options]
 *
 *   primus app register --file app.yaml
 *   primus export run --app LoanService --date 2024-01-01
 *   primus exports list --status COMPLETED
 *   primus approval approve --id req_123 --reason "UAT"
 *   primus audit show --app CreditService --from 2024-01-01 --to 2024-01-31
 * </pre>
 */
public interface Command {
    /** Primary verb (e.g. "app", "export", "approval", "audit", "storage"). */
    String name();

    /** Short description shown in help text. */
    String description();

    /**
     * Execute the command.
     *
     * @param args the remaining arguments after the primary verb
     * @return exit code (0 = success)
     */
    int execute(List<String> args);
}
