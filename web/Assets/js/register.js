const registerButton = document.getElementById('registerButton');
        const usernameInput = document.getElementById('usernameInput');
        const emailInput = document.getElementById('emailInput');
        const passwordInput = document.getElementById('passwordInput');
        const showPasswordCheckbox = document.getElementById('showPassword');
        
        const usernameError = document.getElementById('usernameError');
        const emailError = document.getElementById('emailError');
        const passwordError = document.getElementById('passwordError');

        function showError(element, message) {
            element.textContent = message;
            element.classList.add('show'); 
        }

        function hideError(element) {
            element.textContent = '';
            element.classList.remove('show');
        }

        registerButton.addEventListener('click', function(event) {
            event.preventDefault();

            hideError(usernameError);
            hideError(emailError);
            hideError(passwordError);

            const username = usernameInput.value.trim();
            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            let isValid = true;

            if (username === '') {
                showError(usernameError, 'El usuario es necesario');
                isValid = false;
            }

            if (email === '') {
                showError(emailError, 'El email es obligatorio');
                isValid = false;
            } else if (!/\S+@\S+\.\S+/.test(email)) {
                showError(emailError, 'Formato de email inválido');
                isValid = false;
            }

            if (password === '') {
                showError(passwordError, 'La contraseña es obligatoria');
                isValid = false;
            } else if (password.length < 6) {
                showError(passwordError, 'La contraseña debe tener al menos 6 caracteres');
                isValid = false;
            }
 
          
        });

        showPasswordCheckbox.addEventListener('change', function() {
            passwordInput.type = this.checked ? 'text' : 'password';
        });

        usernameInput.addEventListener('input', () => hideError(usernameError));
        emailInput.addEventListener('input', () => hideError(emailError));
        passwordInput.addEventListener('input', () => hideError(passwordError));

        usernameInput.addEventListener('focus', () => hideError(usernameError));
        emailInput.addEventListener('focus', () => hideError(emailError));
        passwordInput.addEventListener('focus', () => hideError(passwordError));