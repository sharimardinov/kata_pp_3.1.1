// данные с сервера и рендер таблицы
async function loadUsers() {
    try {
        const response = await fetch('http://localhost:8080/api/admin/users');
        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }

        const users = await response.json(); // Преобразуем JSON в объект
        let rows = '';

        users.forEach(user => {
            rows += `
                <tr>
                    <td>${user.id}</td>
                    <td>${user.name}</td>
                    <td>${user.surname}</td>
                    <td>${user.email}</td>
                    <td>${user.age}</td>
                    <td>${user.roles.map(role => role.name).join(', ')}</td>
                    <td>
                        <button class="btn btn-primary edit-btn" data-id="${user.id}" data-bs-toggle="modal" data-bs-target="#editUserModal" >Edit</button>
                    </td>
                    <td>
                        <button class="btn btn-danger delete-btn" data-id="${user.id}" data-bs-toggle="modal" data-bs-target="#deleteUserModal">Delete</button>
                    </td>
                </tr>
            `;
        });
        document.querySelector('#userTable tbody').innerHTML = rows;
    } catch (error) {
        console.error('Failed to load users:', error);
        alert('Failed to load users.');
    }
}

// Загружаем список пользователей при загрузке страницы
document.addEventListener('DOMContentLoaded', loadUsers);



//СОЗДАНИЕ ЮЗЕРА
$('#userForm').submit(async function (event) {
    event.preventDefault();

    // Собираем данные из формы
    const userData = {
        name: $('#name').val(),
        surname: $('#surname').val(),
        email: $('#email').val(),
        age: $('#age').val(),
        password: $('#password').val(),  // Добавляем пароль
        roles: $("#roles").val() ? $('#roles').val().map(roleId => ({
            id: roleId,  // Здесь предполагается, что roleId это ID роли
            name: "USER",  // Пример статического имени
            authority: "USER"  // Статический authority
        })) : [] // Обработаем пустое значение, если роли не выбраны
    };

    console.log("Request Data:", JSON.stringify(userData));

    // Получаем CSRF токен и заголовок
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    if (!csrfToken || !csrfHeader) {
        console.error('CSRF Token or Header is missing!');
        alert('CSRF Token or Header is missing!');
        return;
    }

    // Отправка данных на сервер с использованием fetch
    try {
        const response = await fetch("http://localhost:8080/api/admin/users", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken  // Добавление CSRF токена в заголовки
            },
            body: JSON.stringify(userData)  // Тело запроса с данными пользователя
        });

        if (response.ok) {
            loadUsers(); // Обновляем список пользователей
            // Активируем вкладку с таблицей пользователей
            $('#user-table-tab').tab('show'); // Показываем вкладку с таблицей пользователей

            // Прокручиваем страницу к таблице пользователей
            document.querySelector('#user-table').scrollIntoView({ behavior: 'smooth' });

        } else {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            alert('Error adding user. Please try again.');
        }
    } catch (error) {
        console.error('Failed to add user:', error);
        alert('Failed to add user.');
    }
});




// РЕДАКТИРОВАНИЕ
// Загружаем все роли при редактировании
async function loadRoles() {
    try {
        const response = await fetch('http://localhost:8080/api/admin/roles');
        if (!response.ok) throw new Error(`Error: ${response.status}`);
        const roles = await response.json();

        let options = '';
        roles.forEach(role => {
            options += `<option value="${role.id}">${role.name}</option>`;
        });

        document.querySelector('#editUserRoles').innerHTML = options;
    } catch (error) {
        console.error('Failed to load roles:', error);
    }
}
// Обработчик клика на кнопке эдит
$(document).on('click', '.edit-btn', function () {
    const userId = $(this).data('id');
    $.ajax({
        url: `http://localhost:8080/api/admin/users/${userId}`,
        method: "GET",
        success: function (user) {
            $('#editUserId').val(user.id);
            $('#editUserName').val(user.name);
            $('#editUserSurname').val(user.surname);
            $('#editUserEmail').val(user.email);
            $('#editUserPassword').val(''); // Оставляем пустым
            $('#editUserAge').val(user.age);

            // Загружаем все роли
            loadRoles().then(() => {
                const selectedRoles = user.roles.map(role => role.id); // для формирования списка
                $('#editUserRoles').val(selectedRoles);
            });

            $('#userForm').data('id', userId); // Сохраняем ID пользователя
            $('#userModal').show();
        }
    });
});
// Обработчик нажатия кнопки "редактировать" в модалке
$(document).ready(function() {
    $('#editUserModal').on('show.bs.modal', function (event) {
        const button = $(event.relatedTarget); // Кнопка, которая открывает модалку
        const userId = button.data('id');  // Получаем userId из кнопки
        $('#confirmEditBtn').data('id', userId); // Устанавливаем userId в кнопку confirmEditBtn для дальнейшего использования

        console.log("User ID for editing:", userId); // Проверка, что ID передается
    });

    $(document).on('click', '#confirmEditBtn', async function (event) {
        event.preventDefault();

        const userId = $(this).data('id');
        console.log("User ID:", userId);

        if (!userId) {
            console.error("User ID is missing!");
            alert("User ID is missing!");
            return;
        }

        // Собираем данные из формы
        const userData = {
            id: $('#editUserId').val(),
            name: $('#editUserName').val(),
            surname: $('#editUserSurname').val(),
            email: $('#editUserEmail').val(),
            age: $('#editUserAge').val(),
            // Формируем объект для ролей в соответствии с данными из GET запроса
            roles: $('#editUserRoles').val().map(roleId => ({
                id: roleId,  // Здесь предполагается, что roleId это ID роли
                name: "USER",  // Пример статического имени, если роли это фиксированное значение
                authority: "USER"  // Статический authority, можно изменить по необходимости
            }))
        };

        // Убираем поле пароля, если оно пустое
        if ($("#editUserPassword").val()) {
            userData.password = $('#editUserPassword').val();
        }

        console.log("Request Data:", JSON.stringify(userData));

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        console.log("CSRF Token:", csrfToken);
        console.log("CSRF Header:", csrfHeader);

        try {
            // Отправляем PUT запрос для обновления пользователя
            const response = await fetch(`http://localhost:8080/api/admin/users/${userId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfHeader]: csrfToken  // Добавляем CSRF токен
                },
                body: JSON.stringify(userData)  // Тело запроса с данными пользователя
            });

            if (!response.ok) throw new Error(`Error: ${response.status}`);
            loadUsers();
            $('#editUserModal').modal('hide');  // Используем jQuery метод для закрытия модалки
        } catch (error) {
            console.error('Failed to update user:', error);
            alert('Failed to update user.');
        }
    });
});




// УДАЛЕНИЕ
// ОБРАБОТКА НАЖАТИЯ НА КНОПКУ ДЕЛИТ
console.log("Cookies:", document.cookie);
document.addEventListener('click', async (event) => {
    if (event.target.classList.contains('delete-btn')) {
        const userId = event.target.getAttribute('data-id');

        try {
            // Запрос на сервер, чтобы получить данные пользователя
            const response = await fetch(`http://localhost:8080/api/admin/users/${userId}`);
            if (!response.ok) throw new Error(`Error: ${response.status}`);

            const user = await response.json();

            // Наполнение формы данными пользователя
            document.querySelector('#deleteUserId').value = user.id;
            document.querySelector('#deleteUserName').value = user.name;
            document.querySelector('#deleteUserSurname').value = user.surname;
            document.querySelector('#deleteUserAge').value = user.age;
            document.querySelector('#deleteUserEmail').value = user.email;
            document.querySelector('#deleteUserRoles').value = user.roles.map(role => role.name).join(',');      //не работает!!!

            // Сохраняем ID пользователя для дальнейшего удаления
            document.querySelector('#confirmDeleteBtn').setAttribute('data-id', user.id);

            // Открытие модального окна (если используется JS для Bootstrap)
            const modal = new bootstrap.Modal(document.querySelector('#deleteUserModal'));
            modal.show();
        } catch (error) {
            console.error('Failed to load user data:', error);
            alert('Failed to load user data.');
        }
    }
});
// Обработка удаления пользователя
document.querySelector('#confirmDeleteBtn').addEventListener('click', async function () {
    const userId = this.getAttribute('data-id');

    try {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        const response = await fetch(`http://localhost:8080/api/admin/users/${userId}`, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken  // Добавляем CSRF токен
            }
        });

        if (!response.ok) throw new Error(`Error: ${response.status}`);

        // Закрытие модального окна
        const modal = bootstrap.Modal.getInstance(document.querySelector('#deleteUserModal'));
        modal.hide();
        document.querySelector(`button[data-id="${userId}"]`).closest('tr').remove(); // Удаление строки пользователя из таблицы

    } catch (error) {
        console.error('Failed to delete user:', error);
        alert('Failed to delete user.');
    }
});



// Обработчик события для закрытия модального окна
var myModalEl = document.getElementById('deleteUserModal');
myModalEl.addEventListener('hidden.bs.modal', function () {
    // Сбросить стили фона и прокрутки
    document.body.style.overflow = ''; // Убираем блокировку прокрутки
    const backdrop = document.querySelector('.modal-backdrop');
    if (backdrop) {
        backdrop.remove(); // Удаляем фон, если он не исчезает сам
    }
});




//НАВБАР
$(document).ready(function() {
    // Запрос для получения данных о текущем пользователе
    $.ajax({
        url: 'http://localhost:8080/api/user/profile',  // Путь для получения данных о пользователе
        method: 'GET',  // GET запрос для получения данных
        success: function(response) {
            if (response && response.email && response.roles && response.roles.length > 0) {
                const userEmail = response.email;  // Извлекаем email пользователя
                const userRole = response.roles[0].name;  // Извлекаем первую роль (если есть)

                // Отображаем email и роль в навбаре
                $('#navUserEmail').text(userEmail);
                $('#navUserRole').text(userRole);
            } else {
                alert('Error: Unable to fetch user profile data');
            }
        },
        error: function(error) {
            console.error('Error fetching user profile data:', error);
            alert('Error fetching user profile data');
        }
    });

    // Обработчик клика для кнопки выхода
    $('#logoutButton').on('click', function() {
        window.location.href = 'http://localhost:8080/logout';  // Перенаправление на страницу выхода
    });
});