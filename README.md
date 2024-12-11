# Jet Logistics

**Jet Logistics** — это приложение, разработанное с использованием **Jetpack Compose**, предназначенное для управления логистикой. Проект включает функционал аутентификации пользователей (включая разделы для администраторов), интерактивные интерфейсы работы с календарем, меню и другие функции.

## 🛠️ Основные функции

- **Аутентификация пользователей**: вход с использованием логина и пароля.
- **Роли**: поддержка различных ролей пользователей, включая администраторов.
- **Свайп-навигация**: переход между экранами с использованием жестов.
- **Календарь**: интерактивный календарь для просмотра и управления расписанием.
- **Навигационное меню**: боковое меню с основными разделами.
- **Интеграция с API**: использование `Retrofit` для взаимодействия с сервером.

---

## 🚀 Установка и запуск

### 1. Клонирование репозитория

Склонируйте проект на ваш локальный компьютер:

```bash
git clone https://github.com/SNS1SNS/JetLogistic
cd jet-logistics
```
}
### 2.Запуск
   ## Подключите устройство или запустите эмулятор.
   # Нажмите кнопку "Run" в Android Studio.
## 📂 Структура проекта

```plaintext
├── app/src/main/java/com/example/jet/
│   ├── connection/          # Логика взаимодействия с API
│   ├── model/               # Модели данных (LoginRequest, LoginResponse и т.д.)
│   ├── ui/                  # Компоненты UI, включая экраны и элементы интерфейса
│   ├── MainActivity.kt      # Главная активность приложения
│   ├── LoginActivity.kt     # Экран авторизации пользователей
│   ├── AdminPanelActivity.kt # Экран панели администратора
│   └── ...                  # Другие файлы
└── ...
```
## 📖 Использование

### Аутентификация

- Пользователи могут войти с помощью своего логина и пароля.
- Для администраторов предусмотрен отдельный экран входа.

### Календарь

- Отображает расписание.
- Поддерживает выбор даты и навигацию между месяцами.

### Меню

- Свайп вправо открывает боковое меню с разделами:
   - **Профиль**
   - **Расписание**
   - **Зарплата**
   - **Уведомления**
   - **Сотрудники**

### Свайп-навигация

- Свайп влево позволяет перейти между экранами (например, с панели администратора на экран логина).

---

## 🧰 Технологии

- **Kotlin**: основной язык разработки.
- **Jetpack Compose**: для создания UI.
- **Retrofit**: взаимодействие с API.
- **Material 3**: для стилизации интерфейса.

---

## 🤝 Вклад в проект

Если вы хотите внести изменения:

1. Форкните репозиторий:
   ```bash
   git clone https://github.com/SNS1SNS/JetLogistic
   ```
 ## 📧 Контакты
 # Если у вас есть вопросы или предложения:

 ## Email: sarym.zh@gmail.com
 ## Telegram: @https://t.me/programming_nurs
 ## 🎉 Спасибо за использование Jet Logistics!