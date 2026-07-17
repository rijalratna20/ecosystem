# primus-plugin

A small plugin framework library for Primus. This module provides the plugin SPI (Service Provider Interfaces) that allow custom masking providers, transformers, validators, and storage connectors to be installed into the `primus-server` runtime.

Purpose
- Provide clear SPI interfaces for extensibility
- Allow loading/unloading of plugins (initially classpath-based)
- Keep plugin contracts stable while allowing implementations to evolve

Getting started
- Implement the SPI interfaces in `com.primus.plugin.spi`.
- Package plugin jars and place them on the `primus-server` classpath or install via admin UI (future).

