<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <title>Login & Register | MediScheduler</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login-style.css" />
</head>

<body>

    <!-- Scroll to Top Button -->
    <button id="scrollTopBtn" title="Go to top">▲</button>


    <script>
        //Scroll to Top Button
        const scrollTopBtn = document.getElementById('scrollTopBtn');

        window.addEventListener('scroll', () => {
            if (window.scrollY > 75) {
                scrollTopBtn.classList.add('show');
            } else {
                scrollTopBtn.classList.remove('show');
            }
        });

        scrollTopBtn.addEventListener('click', () => {
            window.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    </script>

    <header class="header-wrapper">
        <nav class="navbar">
            <div class="user-profile">
                <span class="material-symbols-outlined logo-icon">medical_services</span>
                <span class="logo-text">MediScheduler</span>
            </div>

            <button class="hamburger" id="hamburgerBtn" aria-label="Toggle menu">
                <span></span><span></span><span></span>
            </button>

            <ul class="nav-links" id="navLinks">
                <!-- <li><a href="index.jsp">Home</a></li> -->
                <li><button class="btn-primary" onclick="window.location.href='index.jsp'">Back to Home</button></li>
            </ul>
        </nav>
    </header>

    <script>
        const btn = document.getElementById('hamburgerBtn');
        const nav = document.getElementById('navLinks');

        btn.addEventListener('click', () => {
            nav.classList.toggle('open');
            btn.classList.toggle('active');
        });
    </script>

    <main class="page-container">
        <div class="auth-card">

            <!-- LEFT: Branding -->
            <section class="branding-section">
                <svg class="bg-pattern" preserveAspectRatio="none" viewBox="0 0 100 100">
                    <circle cx="20" cy="20" fill="white" r="40"></circle>
                    <circle cx="80" cy="80" fill="white" r="30"></circle>
                </svg>

                <div class="branding-content">
                    <h1>Personalized Care, <br />Simplified.</h1>
                    <p>Manage your appointments, health records, and communications in one secure place.</p>

                    <div class="portal-selection">
                        <p class="toggle-label-text">Account Access Type</p>
                        <div class="portal-toggle">
                            <label class="toggle-option" id="patientToggle">
                                <span>Patient Portal</span>
                                <input type="radio" name="user-type" value="patient" />
                            </label>
                            <label class="toggle-option" id="doctorToggle">
                                <span>Doctor Portal</span>
                                <input type="radio" name="user-type" value="doctor" />
                            </label>
                        </div>
                    </div>
                </div>

                <!-- <div class="compliance-badge">
          <span class="material-symbols-outlined">verified_user</span>
          <p>HIPAA Compliant &amp; Secure Data Encryption</p>
        </div> -->
            </section>

            <!-- RIGHT: Form -->
            <section class="form-section">
                <div class="form-tabs">
                    <button class="tab-trigger" id="signinTab" onclick="switchTab('signin')">Sign In</button>
                    <button class="tab-trigger" id="registerTab" onclick="switchTab('register')">Create Account</button>
                </div>

                <!-- SIGN IN FORM -->
                <div id="signinForm">
                    <div class="form-header">
                        <h2>Welcome Back</h2>
                        <p id="signinSubtitle">Sign in to your Patient Portal.</p>
                    </div>

                    <form onsubmit="handleSignIn(event)">
                        <div class="field-group">
                            <label class="field-label" for="signinEmail">Email Address</label>
                            <div class="field-input-container">
                                <span class="material-symbols-outlined field-icon">mail</span>
                                <input id="signinEmail" class="text-input" placeholder="example@domain.com" type="email"
                                    required />
                            </div>
                        </div>

                        <div class="field-group">
                            <div style="display:flex;justify-content:space-between;align-items:center;">
                                <label class="field-label" for="signinPassword">Password</label>
                                <a href="#" class="forgot-link" onclick="showForgotPassword(event)">Forgot password?</a>
                            </div>
                            <div class="field-input-container">
                                <span class="material-symbols-outlined field-icon">lock</span>
                                <input id="signinPassword" class="text-input" placeholder="••••••••" type="password"
                                    required />
                                <button type="button" class="password-toggle"
                                    onclick="togglePassword('signinPassword', this)">
                                    <span class="material-symbols-outlined">visibility</span>
                                </button>
                            </div>
                        </div>

                        <div class="remember-me">
                            <input type="checkbox" id="remember" />
                            <label for="remember">Remember this device</label>
                        </div>

                        <div id="signinError" class="form-error" style="display:none;"></div>

                        <button class="btn-primary-action" type="submit" id="signinBtn">
                            <span>Sign In to Dashboard</span>
                            <span class="material-symbols-outlined">arrow_forward</span>
                        </button>
                    </form>

                    <!-- <div class="social-divider">
            <div class="social-line"></div>
            <span>Or continue with</span>
            <div class="social-line"></div>
          </div>

          <div class="social-buttons">
            <button class="social-item" onclick="socialLogin('Google')">
              <svg width="18" height="18" viewBox="0 0 48 48"><path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"/><path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"/><path fill="#FBBC05" d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"/><path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.18 1.48-4.97 2.29-8.16 2.29-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"/><path fill="none" d="M0 0h48v48H0z"/></svg>
              Google
            </button>
            <button class="social-item" onclick="socialLogin('Apple')">
              <svg width="18" height="18" viewBox="0 0 814 1000"><path d="M788.1 340.9c-5.8 4.5-108.2 62.2-108.2 190.5 0 148.4 130.3 200.9 134.2 202.2-.6 3.2-20.7 71.9-68.7 141.9-42.8 61.6-87.5 123.1-155.5 123.1s-85.5-39.5-164-39.5c-76 0-103.7 40.8-165.9 40.8s-105-57.8-155.5-127.4C46 790.8 0 663 0 541.8c0-207.5 135.4-317.3 269-317.3 69 0 126.4 45.3 169.6 45.3 41.3 0 106.1-49 184.8-49 30.7 0 130.7 2.6 198.3 99.2zm-234-181.5c31.1-36.9 53.1-88.1 53.1-139.3 0-7.1-.6-14.3-1.9-20.1-50.6 1.9-110.8 33.7-147.1 75.8-28.5 32.4-55.1 83.6-55.1 135.5 0 7.8 1.3 15.6 1.9 18.1 3.2.6 8.4 1.3 13.6 1.3 45.4 0 102.5-30.4 135.5-71.3z"/></svg>
              Apple ID
            </button>
          </div> -->

                    <p class="form-footer-note">
                        Don't have an account?
                        <a href="#" class="footer-link" onclick="switchTab('register'); return false;">Create one
                            here</a>
                    </p>
                </div>

                <!-- REGISTER FORM -->
                <div id="registerForm" style="display:none;">
                    <div class="form-header">
                        <h2>Create Account</h2>
                        <p id="registerSubtitle">Join as a Patient today.</p>
                    </div>

                    <form onsubmit="handleRegister(event)">
                        <div class="field-row">
                            <div class="field-group">
                                <label class="field-label" for="firstName">First Name</label>
                                <div class="field-input-container">
                                    <span class="material-symbols-outlined field-icon">person</span>
                                    <input id="firstName" class="text-input" placeholder="John" type="text" required />
                                </div>
                            </div>
                            <div class="field-group">
                                <label class="field-label" for="lastName">Last Name</label>
                                <div class="field-input-container">
                                    <span class="material-symbols-outlined field-icon">person</span>
                                    <input id="lastName" class="text-input" placeholder="Doe" type="text" required />
                                </div>
                            </div>
                        </div>

                        <div class="field-group">
                            <label class="field-label" for="regEmail">Email Address</label>
                            <div class="field-input-container">
                                <span class="material-symbols-outlined field-icon">mail</span>
                                <input id="regEmail" class="text-input" placeholder="example@domain.com" type="email"
                                    required />
                            </div>
                        </div>

                        <div class="field-group">
                            <label class="field-label" for="regPassword">Password</label>
                            <div class="field-input-container">
                                <span class="material-symbols-outlined field-icon">lock</span>
                                <input id="regPassword" class="text-input" placeholder="Min. 8 characters"
                                    type="password" required minlength="8" />
                                <button type="button" class="password-toggle"
                                    onclick="togglePassword('regPassword', this)">
                                    <span class="material-symbols-outlined">visibility</span>
                                </button>
                            </div>
                        </div>

                        <div class="field-group">
                            <label class="field-label" for="confirmPassword">Confirm Password</label>
                            <div class="field-input-container">
                                <span class="material-symbols-outlined field-icon">lock</span>
                                <input id="confirmPassword" class="text-input" placeholder="Re-enter password"
                                    type="password" required />
                            </div>
                        </div>

                        <div id="registerError" class="form-error" style="display:none;"></div>

                        <button class="btn-primary-action" type="submit" id="registerBtn">
                            <span>Create My Account</span>
                            <span class="material-symbols-outlined">arrow_forward</span>
                        </button>
                    </form>

                    <p class="form-footer-note">
                        Already have an account?
                        <a href="#" class="footer-link" onclick="switchTab('signin'); return false;">Sign in here</a>
                    </p>
                </div>

                <!-- SUCCESS STATE -->
                <div id="successState" style="display:none;" class="success-state">
                    <div class="success-icon">
                        <span class="material-symbols-outlined">check_circle</span>
                    </div>
                    <h2 id="successTitle">Signed In!</h2>
                    <p id="successMsg">Redirecting to your dashboard...</p>
                    <div class="success-loader"></div>
                </div>

                <!-- <p class="form-footer-note" id="termsNote">
          By continuing, you agree to our
          <a href="#" class="footer-link">Terms of Service</a> and
          <a href="#" class="footer-link">Privacy Policy</a>.
        </p> -->
            </section>
        </div>
    </main>

    <script>
        // ---- State ----
        let currentType = 'patient'; // 'patient' | 'doctor'
        let currentTab = 'signin';   // 'signin' | 'register'

        // ---- Init from URL params ----
        function init() {
            const params = new URLSearchParams(window.location.search);
            const type = params.get('type') || 'patient';
            const tab = params.get('tab') || 'signin';
            setType(type);
            switchTab(tab);
        }

        // ---- Toggle patient/doctor ----
        function setType(type) {
            currentType = type;
            const patientToggle = document.getElementById('patientToggle');
            const doctorToggle = document.getElementById('doctorToggle');

            if (type === 'patient') {
                patientToggle.classList.add('active');
                doctorToggle.classList.remove('active');
                patientToggle.querySelector('input').checked = true;
            } else {
                doctorToggle.classList.add('active');
                patientToggle.classList.remove('active');
                doctorToggle.querySelector('input').checked = true;
            }
            updateSubtitles();
        }

        // Clicks on toggle labels
        document.getElementById('patientToggle').addEventListener('click', () => setType('patient'));
        document.getElementById('doctorToggle').addEventListener('click', () => setType('doctor'));

        function updateSubtitles() {
            const label = currentType === 'patient' ? 'Patient Portal' : 'Doctor Portal';
            document.getElementById('signinSubtitle').textContent = `Sign in to your ${label}.`;
            document.getElementById('registerSubtitle').textContent = `Join as a ${currentType === 'patient' ? 'Patient' : 'Doctor'} today.`;
        }

        // ---- Tab switching ----
        function switchTab(tab) {
            currentTab = tab;
            const signinForm = document.getElementById('signinForm');
            const registerForm = document.getElementById('registerForm');
            const successState = document.getElementById('successState');
            const signinTab = document.getElementById('signinTab');
            const registerTab = document.getElementById('registerTab');

            signinForm.style.display = tab === 'signin' ? 'block' : 'none';
            registerForm.style.display = tab === 'register' ? 'block' : 'none';
            successState.style.display = 'none';

            signinTab.classList.toggle('active', tab === 'signin');
            registerTab.classList.toggle('active', tab === 'register');

            clearErrors();
        }

        // ---- Password visibility toggle ----
        function togglePassword(inputId, btn) {
            const input = document.getElementById(inputId);
            const icon = btn.querySelector('.material-symbols-outlined');
            if (input.type === 'password') {
                input.type = 'text';
                icon.textContent = 'visibility_off';
            } else {
                input.type = 'password';
                icon.textContent = 'visibility';
            }
        }

        // ---- Sign In handler ----
        function handleSignIn(e) {
            e.preventDefault();
            clearErrors();
            const email = document.getElementById('signinEmail').value.trim();
            const password = document.getElementById('signinPassword').value;

            if (!email || !password) {
                showError('signinError', 'Please fill in all fields.');
                return;
            }

            const btn = document.getElementById('signinBtn');
            setLoading(btn, true);

            // Simulate async login
            setTimeout(() => {
                setLoading(btn, false);
                showSuccess(
                    `Welcome back!`,
                    `You're signed in as a ${currentType === 'patient' ? 'Patient' : 'Doctor'}. Redirecting to your dashboard...`
                );
            }, 1500);
        }

        // ---- Register handler ----
        function handleRegister(e) {
            e.preventDefault();
            clearErrors();
            const firstName = document.getElementById('firstName').value.trim();
            const lastName = document.getElementById('lastName').value.trim();
            const email = document.getElementById('regEmail').value.trim();
            const password = document.getElementById('regPassword').value;
            const confirm = document.getElementById('confirmPassword').value;

            if (!firstName || !lastName || !email || !password || !confirm) {
                showError('registerError', 'Please fill in all fields.');
                return;
            }
            if (password !== confirm) {
                showError('registerError', 'Passwords do not match.');
                return;
            }
            if (password.length < 8) {
                showError('registerError', 'Password must be at least 8 characters.');
                return;
            }

            const btn = document.getElementById('registerBtn');
            setLoading(btn, true);

            setTimeout(() => {
                setLoading(btn, false);
                showSuccess(
                    'Account Created!',
                    `Your ${currentType === 'patient' ? 'Patient' : 'Doctor'} account has been created. Redirecting...`
                );
            }, 1500);
        }

        // ---- Social login ----
        function socialLogin(provider) {
            showSuccess(`Connecting to ${provider}...`, 'Redirecting you to complete authentication.');
        }

        // ---- Forgot password ----
        function showForgotPassword(e) {
            e.preventDefault();
            const email = document.getElementById('signinEmail').value.trim();
            if (!email) {
                showError('signinError', 'Please enter your email address first.');
                return;
            }
            showSuccess('Reset Email Sent!', `A password reset link has been sent to ${email}.`);
        }

        // ---- Helpers ----
        function showError(id, msg) {
            const el = document.getElementById(id);
            el.textContent = msg;
            el.style.display = 'block';
        }

        function clearErrors() {
            ['signinError', 'registerError'].forEach(id => {
                const el = document.getElementById(id);
                if (el) { el.style.display = 'none'; el.textContent = ''; }
            });
        }

        function setLoading(btn, loading) {
            if (loading) {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner"></span><span>Please wait...</span>';
            } else {
                btn.disabled = false;
                btn.innerHTML = `<span>${currentTab === 'signin' ? 'Sign In to Dashboard' : 'Create My Account'}</span><span class="material-symbols-outlined">arrow_forward</span>`;
            }
        }

        function showSuccess(title, msg) {
            document.getElementById('signinForm').style.display = 'none';
            document.getElementById('registerForm').style.display = 'none';
            document.getElementById('termsNote').style.display = 'none';
            document.getElementById('successTitle').textContent = title;
            document.getElementById('successMsg').textContent = msg;
            document.getElementById('successState').style.display = 'flex';
        }

        // Run on load
        init();
    </script>
</body>

</html>