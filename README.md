# LootRush

---

## English

A mini-game for Minecraft servers based on Paper, where players compete to be the first to obtain a random item.

### Main Features

- **Random Item** - the system automatically selects an item from the pool of available items
- **Role System** - support for player and spectator roles
- **Lives System** - each player has 5 lives, loses one life upon death
- **Random Teleportations** - players randomly swap positions every 5 minutes
- **Safe Teleportation** - automatic search for safe locations for players
- **Damage Protection** - players receive invincibility for 25 seconds after teleportation
- **Multi-language Support** - available in English, Russian, and Ukrainian

### Installation

1. Download the latest version of the plugin from Modrinth/CurseForge (you can also use Releases)
2. Place the `LootRush.jar` file in your server's `plugins` folder or in the `mods` folder for the mod
3. Restart the server or the game for the mod
4. The plugin/mod is ready to use!

### Configuration

The plugin generates a `config.yml` file in the `plugins/LootRush` folder.
You can configure the list of banned items that will not be selected as targets.

```yaml
# Configuration for LootRush
# banned-items: List of items that should not appear in the game.
# You can use exact item names (e.g. BEDROCK) or regex patterns starting with "REGEX:"

# swap-interval-seconds: Interval between player substitutions in seconds (default 300 seconds = 5 minutes)
swap-interval-seconds: 300

# scatter-min-coord: Min coord for teleportation (default 10000)
scatter-min-coord: 10000

# scatter-max-coord: Max coord for teleportation (default 100000)
scatter-max-coord: 100000

banned-items:
  - BEDROCK
  - BARRIER
  - REGEX:.*_SPAWN_EGG
  # ... and others
```

### Mod Configuration (NeoForge)

The mod uses the standard NeoForge config file: `config/lootrush-common.toml`.

Example:

```toml
swap-interval-seconds = 300
scatter-min-coord = 10000
scatter-max-coord = 100000

banned-items = [
  "minecraft:bedrock",
  "minecraft:barrier",
  "REGEX:.*_spawn_egg"
]
```

### Requirements

- **Minecraft**: 1.21.x
- **Server**: Paper/Bukkit 1.21 or higher
- **Java**: 17 or higher

### Commands

#### `/lootrush` (or `/lr`)

Main command for managing the mini-game.

##### Subcommands:

- `/lootrush start` - starts a new game

    - Teleports all players with the "player" role to random locations
    - Selects a random item
    - Starts a 10-second countdown

- `/lootrush stop` - stops the current game

    - Clears all game data
    - Returns players to spawn
    - Clears inventories

- `/lootrush status` - shows the current game status

    - Shows current state (IDLE, COUNTDOWN, ACTIVE)
    - Shows the current target item

- `/lootrush skip` - skips the current item (only during active game)

    - Selects a new random item

- `/lootrush role <player|spectator> [player|selector]` - changes a player's role
    - `player` - player participates in the mini-game
    - `spectator` - player watches the game
    - If role is not specified, the command applies to the executor (players only)
    - Supports Minecraft selectors (@a, @p, @r, @s, @e[type=player])

- `/lootrush lang <en|ru|ua>` - changes language
    - `en` - English
    - `ru` - Russian
    - `ua` - Ukrainian

- `/lootrush debug` - toggles debug mode
    - Enables extra logs for chunk loading and safe location search

##### Permissions

- `lootrush.admin` - permission for all commands (default: operators only)

### Usage

#### Quick Start

1. Set player roles:

   ```
   /lootrush role player @a
   ```

2. Start the game:

   ```
   /lootrush start
   ```

3. Players will be teleported to random locations and the countdown will begin

4. The first player to obtain the target item wins!

#### Usage Examples

**Set spectator role for a specific player:**

```
/lootrush role spectator howegf
```

**Set player role for everyone:**

```
/lootrush role player @a
```

**Change language:**

```
/lootrush lang [en/ru/ua]
```

**Stop the game:**

```
/lootrush stop
```

### Game Mechanics

#### Lives System

- Each player starts with 5 lives
- Upon death, a player loses one life
- When lives run out, the player is automatically converted to a spectator
- Lives are displayed in the scoreboard

#### Random Teleportations

- Teleportation happens at the swap interval
- The target item bossbar shows progress to the next swap
- After teleportation, players receive invincibility for 25 seconds

#### Item Selection

The plugin automatically filters items, excluding:

- Items that cannot be obtained in survival mode
- Technical blocks (command blocks, barriers, etc.)
- Items configured in `config.yml`

### Known Limitations

- Teleportation may take some time with a large number of players due to chunk loading
- Chunk loading speed depends on the host disk
- The game works only in the overworld (not in Nether/End)

### License

MIT License

### Author

## **Nasarwo (DaGGeR)**

For questions and suggestions, please create an issue in the project repository.

---

## Русский

Мини-игра для Minecraft серверов на базе Paper, в которой игроки соревнуются, кто первым добудет случайный предмет.

### Основные функции

- **Случайный предмет** - система автоматически выбирает предмет из пула доступных предметов
- **Система ролей** - поддержка ролей игрока и наблюдателя
- **Система жизней** - каждый игрок имеет 5 жизней, после смерти теряет одну жизнь
- **Случайные телепортации** - игроки случайно меняются местами каждые 5 минут
- **Безопасная телепортация** - автоматический поиск безопасных локаций для игроков
- **Защита от урона** - игроки получают неуязвимость на 25 секунд после телепортации
- **Мультиязычность** - поддержка русского, английского и украинского языков

### Установка

1. Скачайте последнюю версию плагина из Modrinth/CurseForge (можно использовать и Releases в репозитории)
2. Поместите файл `LootRush.jar` в папку `plugins` вашего сервера или в папку `mods` для мода
3. Перезапустите сервер или игру в случае мода
4. Плагин/мод готов к использованию!

### Конфигурация

Плагин создает файл `config.yml` в папке `plugins/LootRush`.
Вы можете настроить список запрещенных предметов, которые не будут выбираться в качестве цели.

```yaml
# Конфигурация LootRush
# banned-items: Список предметов, которые не должны появляться в игре.
# Можно использовать точные названия (например, BEDROCK) или регулярні выражения, начинающиеся с "REGEX:"

# swap-interval-seconds: Интервал между сменами мест в секундах (по умолчанию 300 секунд = 5 минут)
swap-interval-seconds: 300

# scatter-min-coord: Минимальная координата для телепортации (по умолчанию 10000)
scatter-min-coord: 10000

# scatter-max-coord: Максимальная координата для телепортации (по умолчанию 100000)
scatter-max-coord: 100000

banned-items:
  - BEDROCK
  - BARRIER
  - REGEX:.*_SPAWN_EGG
  # ... и другие
```

### Конфигурация мода (NeoForge)

Мод использует стандартный конфиг NeoForge: `config/lootrush-common.toml`.

Пример:

```toml
swap-interval-seconds = 300
scatter-min-coord = 10000
scatter-max-coord = 100000

banned-items = [
  "minecraft:bedrock",
  "minecraft:barrier",
  "REGEX:.*_spawn_egg"
]
```

### Требования

- **Minecraft**: 1.21.x
- **Сервер**: Paper/Bukkit 1.21 или выше
- **Java**: 17 или выше

### Команди

#### `/lootrush` (или `/lr`)

Основная команда для управления мини-игрой.

##### Подкоманды:

- `/lootrush start` - запускает новую игру

    - Телепортирует всех игроков с ролью "player" на случайные локации
    - Выбирает случайный предмет
    - Начинает отсчёт 10 секунд

- `/lootrush stop` - останавливает текущую игру

    - Очищает все данные игры
    - Возвращает игроков на спавн
    - Очищает инвентари

- `/lootrush status` - показывает текущий статус игры

    - Показывает текущее состояние (IDLE, COUNTDOWN, ACTIVE)
    - Показывает текущий искомый предмет

- `/lootrush skip` - пропускает текущий предмет (только во время активной игры)

    - Выбирает новый случайный предмет

- `/lootrush role <player|spectator> [игрок|селектор]` - изменяет роль игрока
    - `player` - игрок участвует в мини-игре
    - `spectator` - игрок наблюдает за игрой
    - Если роль не указана, команда применяется к исполнителю (только для игроков)
    - Поддерживает селекторы Minecraft (@a, @p, @r, @s, @e[type=player])

- `/lootrush lang <ru|en|ua>` - изменяет язык
    - `ru` - Русский
    - `en` - Английский
    - `ua` - Украинский

- `/lootrush debug` - переключает режим отладки
    - Включает дополнительные логи для загрузки чанков и поиска безопасных локаций

##### Разрешения

- `lootrush.admin` - разрешение для всех команд (по умолчанию только для операторов)

### Использование

#### Быстрый старт

1. Установите роли игрокам:

   ```
   /lootrush role player @a
   ```

2. Запустить игру:

   ```
   /lootrush start
   ```

3. Игроки будут телепортированы на случайные локации, и начнётся отсчёт

4. Первый игрок, который добудет искомый предмет, побеждает!

#### Примеры использования

**Установить роль наблюдателя для конкретного игрока:**

```
/lootrush role spectator howegf
```

**Установить роль игрока для всех:**

```
/lootrush role player @a
```

**Сменить язык:**

```
/lootrush lang [en/ru/ua]
```

**Остановить игру:**

```
/lootrush stop
```

### Механика игры

#### Система жизней

- Каждый игрок начинает с 5 жизнями
- При смерти игрок теряет одну жизнь
- Когда жизни заканчиваются, игрок автоматически переводится в наблюдатели
- Жизни отображаются в scoreboard

#### Случайные телепортации

- Телепортация происходит через интервал свапа
- Боссбар цели отсчитывает время до следующего свапа
- После телепортации игроки получают неуязвимость на 25 секунд

#### Выбор предметов

Плагин автоматически фильтрует предметы, исключая:

- Предметы, которые невозможно получить в выживании
- Технические блоки (командные блоки, барьеры и т.д.)
- Предметы, настроенные в `config.yml`

### Известные ограничения

- Телепортация может занять некоторое время при большом количестве игроков из-за загрузки чанков
- Скорость загрузки чанков зависит от диска хоста
- Игра работает только в обычном мире (не в Nether/End)

### Лицензия

MIT License

### Автор

## **Nasarwo (DaGGeR)**

Для вопросов и предложений создайте issue в репозитории проекта.

---
