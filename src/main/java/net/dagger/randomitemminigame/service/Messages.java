package net.dagger.randomitemminigame.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Messages {
	public static Component get(LanguageService.Language lang, MessageKey key, Object... args) {
		String message = getStringPrivate(lang, key, args);
		NamedTextColor color = getColor(key);
		return Component.text(message, color);
	}

	public static Component get(LanguageService.Language lang, MessageKey key, NamedTextColor color, Object... args) {
		String message = getStringPrivate(lang, key, args);
		return Component.text(message, color);
	}

	public static String getString(LanguageService.Language lang, MessageKey key, Object... args) {
		String template = lang == LanguageService.Language.EN ? key.english : key.russian;
		return String.format(template, args);
	}

	private static String getStringPrivate(LanguageService.Language lang, MessageKey key, Object... args) {
		return getString(lang, key, args);
	}

	private static NamedTextColor getColor(MessageKey key) {
		return key.defaultColor;
	}

	public enum MessageKey {
		USAGE("/lootrush <start|stop|status|role|cancel|skip|lang>", "/lootrush <start|stop|status|role|cancel|skip|lang>", NamedTextColor.YELLOW),
		UNKNOWN_SUBCOMMAND("Неизвестная подкоманда. Используйте: start, stop, status, role, cancel, skip или lang.", "Unknown subcommand. Use: start, stop, status, role, cancel, skip or lang.", NamedTextColor.RED),
		GAME_ALREADY_RUNNING("Мини-игра уже запущена или идёт отсчёт.", "The minigame is already running or countdown is in progress.", NamedTextColor.RED),
		NO_PLAYERS("Нет игроков, участвующих в мини-игре. Используйте /lootrush role player.", "No players participating in the minigame. Use /lootrush role player.", NamedTextColor.RED),
		RANDOM_ITEM_HEADER("=== Случайный предмет ===", "=== Random Item ===", NamedTextColor.GOLD),
		NEED_TO_OBTAIN("Нужно первым добыть: ", "You need to obtain first: ", NamedTextColor.YELLOW),
		PLAYERS_TELEPORTED("Игроки телепортированы. Отсчёт %d сек...", "Players teleported. Countdown %d sec...", NamedTextColor.GRAY),
		GAME_ALREADY_STOPPED("Мини-игра и так остановлена.", "The minigame is already stopped.", NamedTextColor.RED),
		GAME_STOPPED("Мини-игра остановлена администратором.", "The minigame was stopped by an administrator.", NamedTextColor.RED),
		NO_COUNTDOWN("Нет активного отсчёта или загрузки для прерывания. Используйте /lootrush stop для остановки игры.", "No active countdown or loading to interrupt. Use /lootrush stop to stop the game.", NamedTextColor.RED),
		GAME_CANCELLED("Начало игры прервано администратором.", "Game start was cancelled by an administrator.", NamedTextColor.RED),
		GAME_NOT_ACTIVE("Игра не активна. Пропуск предмета возможен только во время активной игры.", "Game is not active. Skipping item is only possible during an active game.", NamedTextColor.RED),
		NO_CURRENT_ITEM("Нет текущего предмета для пропуска.", "No current item to skip.", NamedTextColor.RED),
		ITEM_SKIPPED("Предмет пропущен администратором. ", "Item skipped by administrator. ", NamedTextColor.YELLOW),
		NEW_ITEM("Новый предмет: ", "New item: ", NamedTextColor.GOLD),
		ITEM_CHANGED("Предмет изменён с ", "Item changed from ", NamedTextColor.GREEN),
		ITEM_CHANGED_TO(" на ", " to ", NamedTextColor.GREEN),
		GAME_NOT_STARTED("Мини-игра не запущена.", "The minigame is not started.", NamedTextColor.GREEN),
		COUNTDOWN_IN_PROGRESS("Идёт отсчёт. Цель: ", "Countdown in progress. Target: ", NamedTextColor.YELLOW),
		GAME_ACTIVE("Игра активна! Цель: ", "Game active! Target: ", NamedTextColor.AQUA),
		NO_PERMISSION_ROLE("Недостаточно прав для изменения ролей.", "Insufficient permissions to change roles.", NamedTextColor.RED),
		ROLE_USAGE("Использование: /lootrush role <player|spectator> [ник|селектор]", "Usage: /lootrush role <player|spectator> [player|selector]", NamedTextColor.YELLOW),
		UNKNOWN_ROLE("Неизвестная роль. Доступно: player, spectator.", "Unknown role. Available: player, spectator.", NamedTextColor.RED),
		SELECTOR_NO_PLAYERS("Селектор %s не нашёл игроков.", "Selector %s found no players.", NamedTextColor.RED),
		INVALID_SELECTOR("Неверный селектор: %s", "Invalid selector: %s", NamedTextColor.RED),
		PLAYER_NOT_FOUND("Игрок %s не найден.", "Player %s not found.", NamedTextColor.RED),
		NEED_PLAYER_OR_SELECTOR("Нужно указать ник игрока или селектор.", "You need to specify a player name or selector.", NamedTextColor.YELLOW),
		ROLE_SET_BY_ADMIN("Администратор установил вам роль %s.", "An administrator set your role to %s.", NamedTextColor.AQUA),
		ROLE_SET_SINGLE("Установлена роль %s для %s.", "Role %s set for %s.", NamedTextColor.GREEN),
		ROLE_SET_MULTIPLE("Установлена роль %s для %d игроков.", "Role %s set for %d players.", NamedTextColor.GREEN),
		LANG_USAGE("Использование: /lootrush lang <ru|en>", "Usage: /lootrush lang <ru|en>", NamedTextColor.YELLOW),
		UNKNOWN_LANGUAGE("Неизвестный язык. Доступно: ru, en.", "Unknown language. Available: ru, en.", NamedTextColor.RED),
		LANGUAGE_SET("Язык изменён на %s.", "Language changed to %s.", NamedTextColor.GREEN),
		CURRENT_LANGUAGE("Текущий язык: %s", "Current language: %s", NamedTextColor.AQUA),
		NOW_PARTICIPATING("Теперь вы участвуете в мини-игре.", "You are now participating in the minigame.", NamedTextColor.GREEN),
		NOW_SPECTATOR("Вы перешли в режим наблюдателя.", "You switched to spectator mode.", NamedTextColor.AQUA),
		SPECTATING_ROUND("Вы наблюдаете за раундом как зритель.", "You are spectating the round as a viewer.", NamedTextColor.GRAY),
		LOADING_CHUNKS("Загружаем чанки для телепортации...", "Loading chunks for teleportation...", NamedTextColor.YELLOW),
		TELEPORTED_TO("Вы телепортированы в %s", "You have been teleported to %s", NamedTextColor.GRAY),
		SEARCHING_LOCATION("Ищем место для %s...", "Searching location for %s...", NamedTextColor.YELLOW),
		LOCATION_FOUND("Нашли место для %s: %s", "Found location for %s: %s", NamedTextColor.GREEN),
		ATTEMPT_TOO_CLOSE("Попытка #%d для %s: (%d, ???, %d) слишком близко к другим игрокам", "Attempt #%d for %s: (%d, ???, %d) too close to other players", NamedTextColor.GRAY),
		ATTEMPT_Y_TOO_LOW("Попытка #%d для %s: (%d, %d) отклонена — Y ниже минимума", "Attempt #%d for %s: (%d, %d) rejected — Y below minimum", NamedTextColor.GRAY),
		ATTEMPT_LOCATION_FOUND("Попытка #%d для %s: найдена точка (%d, %d, %d)", "Attempt #%d for %s: found location (%d, %d, %d)", NamedTextColor.GREEN),
		ATTEMPT_UNSAFE_BLOCKS("Попытка #%d для %s: (%d, %d, %d) отклонена — блоки небезопасны (floor=%s, feet=%s, head=%s)", "Attempt #%d for %s: (%d, %d, %d) rejected — blocks unsafe (floor=%s, feet=%s, head=%s)", NamedTextColor.GRAY),
		LOADING_NEAR_CHUNKS("Загружаем ближние чанки: ", "Loading nearby chunks: ", NamedTextColor.YELLOW),
		CHUNKS_TEXT(" чанков...", " chunks...", NamedTextColor.YELLOW),
		CHUNK_LOADED("Загружен чанк ", "Loaded chunk ", NamedTextColor.GRAY),
		CHUNK_LOADED_WITH_COORDS("Загружен чанк [%d, %d] (%d/%d)", "Loaded chunk [%d, %d] (%d/%d)", NamedTextColor.GRAY),
		NEAR_CHUNKS_LOADED("Ближние чанки загружены! ", "Nearby chunks loaded! ", NamedTextColor.GREEN),
		LOADING_FAR_CHUNKS("Загружаем дальние чанки: ", "Loading distant chunks: ", NamedTextColor.YELLOW),
		ALL_CHUNKS_LOADED("Все чанки загружены! ", "All chunks loaded! ", NamedTextColor.GREEN),
		CHUNKS_COUNT(" (%d чанков)", " (%d chunks)", NamedTextColor.GRAY),
		CHUNK_LOAD_ERROR("Ошибка при прогрузке чанка: %s", "Error loading chunk: %s", NamedTextColor.RED),
		SCATTER_BOSS_BAR("Поиск безопасных локаций... %d/%d", "Searching safe locations... %d/%d", NamedTextColor.BLUE),
		TELEPORTATION_COMPLETE("Телепортация завершена", "Teleportation complete", NamedTextColor.GREEN),
		TELEPORTATION_STOPPED("Телепортация остановлена", "Teleportation stopped", NamedTextColor.RED),
		SWAP_IN_SECONDS("Случайная смена мест через %d секунд!", "Random location swap in %d seconds!", NamedTextColor.LIGHT_PURPLE),
		SWAP_IN_SECONDS_SHORT("Смена мест через %d секунд.", "Location swap in %d seconds.", NamedTextColor.LIGHT_PURPLE),
		SWAP_IN_MINUTE("Случайная смена мест через 1 минуту!", "Random location swap in 1 minute!", NamedTextColor.YELLOW),
		SWAP_IN_30_SECONDS("Случайная смена мест через 30 секунд!", "Random location swap in 30 seconds!", NamedTextColor.YELLOW),
		SWAP_STARTING("Случайная смена мест начнётся через %d секунд!", "Random location swap will start in %d seconds!", NamedTextColor.LIGHT_PURPLE),
		PLAYERS_SWAPPING("Игроки меняются местами!", "Players are swapping locations!", NamedTextColor.LIGHT_PURPLE),
		LIVES("Жизни", "Lives", NamedTextColor.RED),
		TIME("Время: ", "Time: ", NamedTextColor.GOLD),
		START_GOOD_LUCK("Старт! Удачи в поисках ", "Start! Good luck finding ", NamedTextColor.GREEN),
		START_IN_SECONDS("Старт через %d...", "Start in %d...", NamedTextColor.YELLOW),
		NO_LIVES_LEFT("Вы исчерпали все жизни и теперь наблюдаете за раундом.", "You have run out of lives and are now spectating the round.", NamedTextColor.RED),
		PLAYER_OUT("%s исчерпал все жизни и выбыл из игры.", "%s has run out of lives and is out of the game.", NamedTextColor.GRAY),
		LIVES_REMAINING("У вас осталось жизней: %d из %d", "You have %d lives remaining out of %d", NamedTextColor.YELLOW),
		PLAYER_WON("Игрок ", "Player ", NamedTextColor.GOLD),
		OBTAINED_FIRST(" первым добыл ", " obtained first ", NamedTextColor.GOLD),
		AND_WON(" и победил!", " and won!", NamedTextColor.GOLD),
		LAST_PLAYER_STANDING("Игрок остался один в живых: ", "Last player standing: ", NamedTextColor.GOLD),
		WINS_ROUND(" и выигрывает раунд!", " wins the round!", NamedTextColor.GOLD),
		RETURNING_TO_SPAWN("Возвращаем на спавн и очищаем инвентарь после раунда.", "Returning to spawn and clearing inventory after the round.", NamedTextColor.GRAY);

		private final String russian;
		private final String english;
		private final NamedTextColor defaultColor;

		MessageKey(String russian, String english, NamedTextColor defaultColor) {
			this.russian = russian;
			this.english = english;
			this.defaultColor = defaultColor;
		}
	}
}
