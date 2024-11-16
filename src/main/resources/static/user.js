// данные с сервера и рендер таблицы
async function loadUsers() {
    try {
        const response = await fetch('http://localhost:8080/api/user/profile');
        if (!response.ok) throw new Error(`Error: ${response.status}`);

        const user = await response.json(); // Преобразуем JSON в объект

        // Заполняем таблицу данными пользователя
        document.querySelector('#userTable tbody').innerHTML = `
        <tr>
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.surname}</td>
            <td>${user.age}</td>
            <td>${user.email}</td>
            <td>${user.roles.map(role => role.name).join(', ')}</td>
        </tr>
        `;
    } catch (error) {
        console.error('Failed to load user:', error);
        alert('Failed to load user.');
    }
}


// Загружаем список пользователей при загрузке страницы
document.addEventListener('DOMContentLoaded', loadUsers);



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
