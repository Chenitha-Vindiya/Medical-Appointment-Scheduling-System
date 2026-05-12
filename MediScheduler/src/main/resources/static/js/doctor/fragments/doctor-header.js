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

// Dark mode toggle logic
const themeToggle = document.getElementById('theme-toggle');
const themeIcon = document.getElementById('theme-icon');

// Check for saved user preference on load
if (localStorage.getItem('theme') === 'dark') {
    document.body.classList.add('dark-mode');
    themeIcon.innerText = 'dark_mode'; // Changes icon to moon
} else {
    themeIcon.innerText = 'light_mode'; // Changes icon to sun
}

themeToggle.addEventListener('click', () => {
    document.body.classList.toggle('dark-mode');
    const isDark = document.body.classList.contains('dark-mode');

    // Update icon text and save preference
    themeIcon.innerText = isDark ? 'dark_mode' : 'light_mode';
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
});