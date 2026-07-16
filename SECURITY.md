# Security Notice

**This repository is unmaintained.** TotalFreedomMod has not been actively developed for many years. Known and unknown security vulnerabilities may exist in this codebase and in any binaries built from it.

Formal disclosures are published as [GitHub Security Advisories](https://github.com/TotalFreedom/TotalFreedomMod/security/advisories). This file is a short in-repo summary for anyone browsing the source.

## Do not use in production

Do **not** run releases or builds from this repository on a production Minecraft server, or on any server exposed to the public internet, unless you have independently audited and hardened the code yourself.

Use of this software is **at your own risk**. The copyright holders and contributors provide no warranty and accept no responsibility for damage, compromise, or data loss resulting from its use.

## Known concerns

As of July 2026, critical issues in the built-in HTTPD feature have been reported and confirmed in this repository's source. See the [Security Advisories](https://github.com/TotalFreedom/TotalFreedomMod/security/advisories) for the formal record.

HTTPD is **enabled by default** in the bundled `config.yml` (default port `28966`). In particular:

- The unauthenticated `/dump` HTTP endpoint accepts file uploads and uses insufficient path checks, which can allow writing files outside the intended upload directory (including under `plugins/`), potentially leading to remote code execution after a server restart.
- The `/logfile` HTTP endpoint (restricted to active superadmin IPs) joins a user-supplied filename under `./logs/` with no path validation, which can allow reading arbitrary files from the host.

These issues affect historical releases and source in this repository. **If HTTPD is enabled and reachable, treat the server as remotely exploitable.**

Setting `httpd.enabled` to `false` and restarting reduces exposure from those endpoints, but does **not** mean the rest of the plugin is safe or supported. Do not expose the HTTPD port to untrusted networks.

## Reporting issues

This project is not under active security maintenance. There is no guaranteed response to vulnerability reports.

Check existing [Security Advisories](https://github.com/TotalFreedom/TotalFreedomMod/security/advisories) first. If you still wish to report a new issue for archival purposes, open a draft advisory there (preferred) or contact a repository owner. Please do not file public issues that include working exploit details.

## Prefer maintained forks

If you need a TotalFreedom-style plugin for a live server, use a **maintained fork** that has addressed these issues, and verify that claim yourself. Do not assume that any fork is safe solely because it exists.
