const loginButton = document.getElementById('loginButton');
        const usernameInput = document.getElementById('usernameInput');
        const passwordInput = document.getElementById('passwordInput');
        const usernameError = document.getElementById('usernameError');
        const passwordError = document.getElementById('passwordError');

        function showError(element, message) {
            element.textContent = message;
            element.classList.add('show'); 
        }

        function hideError(element) {
            element.textContent = '';
            element.classList.remove('show');
        }

        loginButton.addEventListener('click', function(event) {
            event.preventDefault();

            hideError(usernameError);
            hideError(passwordError);

            const username = usernameInput.value.trim();
            const password = passwordInput.value.trim();

            let isValid = true;

            if (username === '') {
                showError(usernameError, 'Upss tu usuario es necesario');
                isValid = false;
            }

            if (password === '') {
                showError(passwordError, 'La contraseña es obligatoria');
                isValid = false;
            }

            if (isValid) {
                alert('¡Datos completos!');
            }
        });

        usernameInput.addEventListener('input', function() {
            hideError(usernameError);
        });

        passwordInput.addEventListener('input', function() {
            hideError(passwordError);
        });

        usernameInput.addEventListener('focus', function() {
            hideError(usernameError);
        });

        passwordInput.addEventListener('focus', function() {
            hideError(passwordError);
        });