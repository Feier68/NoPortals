# NoPortals

NoPortals is a Paper plugin for Minecraft 1.21.11 that prevents players from entering Nether and End portals.
If a blocked player tries to enter one of these portals, the plugin can push the player back and send a configurable MiniMessage chat message.

## Features

- Block Nether portal entry
- Block End portal entry
- Configurable pushback toggle and strength
- Configurable MiniMessage messages
- `/noportals reload` command for admins

## Commands

- `/noportals reload` — reloads `config.yml`

## Permission

- `noportals.reload` — allows using `/noportals reload`

## Configuration

```yml
block-nether: true
block-end: true
pushback: true
pushback-strength: 1.0
messages:
  nether-blocked: "<red>Du darfst Nether-Portale nicht betreten.</red>"
  end-blocked: "<red>Du darfst End-Portale nicht betreten.</red>"
```

## Build

```bash
./gradlew build
```

The release jar is generated in `build/libs/`.

## Modrinth Release Setup

This repository is prepared for Modrinth publishing via GitHub Actions.
To enable it, set these repository values:

- GitHub Actions secret: `MODRINTH_TOKEN`
- `gradle.properties`: replace `modrinth_project_id=SET_ME` with your Modrinth project slug or ID

## License

MIT
