// Scroll to Top Button
const scrollTopBtn = document.getElementById('scrollTopBtn');
window.addEventListener('scroll', () => {
    if (window.scrollY > 75) {
        scrollTopBtn.classList.add('show');
    } else {
        scrollTopBtn.classList.remove('show');
    }
});
scrollTopBtn.addEventListener('click', () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
});

// Mobile hamburger menu
const btn = document.getElementById('hamburgerBtn');
const nav = document.getElementById('navLinks');
btn.addEventListener('click', () => {
    nav.classList.toggle('open');
    btn.classList.toggle('active');
});

// Close menu on link click
nav.querySelectorAll('a, button').forEach(el => {
    el.addEventListener('click', () => {
        nav.classList.remove('open');
        btn.classList.remove('active');
    });
});

// Dark mode toggle
const themeToggle = document.getElementById('theme-toggle');
const themeIcon = document.getElementById('theme-icon');
if (localStorage.getItem('theme') === 'dark') {
    document.body.classList.add('dark-mode');
    themeIcon.innerText = 'dark_mode';
}
themeToggle.addEventListener('click', () => {
    document.body.classList.toggle('dark-mode');
    const isDark = document.body.classList.contains('dark-mode');
    themeIcon.innerText = isDark ? 'dark_mode' : 'light_mode';
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
});