document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registrationForm");
    const nomeInput = document.getElementById("nome");
    const cognomeInput = document.getElementById("cognome");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passwordRegex = /^.{6,}$/; 

    nomeInput.addEventListener("change", validaNome);
    cognomeInput.addEventListener("change", validaCognome);
    emailInput.addEventListener("change", validaEmail);
    passwordInput.addEventListener("change", validaPassword);

    function validaNome() {
        const errorSpan = document.getElementById("nomeError");
        if (nomeInput.value.trim().length < 2) {
            errorSpan.textContent = "IL NOME DEVE CONTENERE ALMENO 2 CARATTERI.";
            return false;
        } else {
            errorSpan.textContent = "";
            return true;
        }
    }

    function validaCognome() {
        const errorSpan = document.getElementById("cognomeError");
        if (cognomeInput.value.trim().length < 2) {
            errorSpan.textContent = "IL COGNOME DEVE CONTENERE ALMENO 2 CARATTERI.";
            return false;
        } else {
            errorSpan.textContent = "";
            return true;
        }
    }

    function validaEmail() {
        const errorSpan = document.getElementById("emailError");
        if (!emailRegex.test(emailInput.value.trim())) {
            errorSpan.textContent = "FORMATO EMAIL NON VALIDO (ES: PILOTA@SCUDERIA.COM).";
            return false;
        } else {
            errorSpan.textContent = "";
            return true;
        }
    }

    function validaPassword() {
        const errorSpan = document.getElementById("passwordError");
        if (!passwordRegex.test(passwordInput.value)) {
            errorSpan.textContent = "STRUTTURA DEBOLE: LA PASSWORD DEVE CONTENERE MINIMO 6 CARATTERI.";
            return false;
        } else {
            errorSpan.textContent = "";
            return true;
        }
    }

    form.addEventListener("submit", function (event) {
        const isNomeValid = validaNome();
        const isCognomeValid = validaCognome();
        const isEmailValid = validaEmail();
        const isPasswordValid = validaPassword();

        if (!isNomeValid || !isCognomeValid || !isEmailValid || !isPasswordValid) {
            event.preventDefault();
        }
    });
});