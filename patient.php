<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <title>Patient Directory | ClinicConnect</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet" />

    <style>
        :root {
            /* Core Palette */
            --primary: #1e40af;
            --primary-light: #eff6ff;
            --background: #f8fafc;
            --card-bg: #ffffff;
            --border-color: #e2e8f0;

            /* Text Colors */
            --text-main: #1e293b;
            --text-muted: #64748b;
            --text-light: #94a3b8;

            /* Status Colors */
            --active-bg: #dcfce7;
            --active-text: #15803d;
            --inactive-bg: #f1f5f9;

            /* Dimensions */
            --sidebar-width: 260px;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--background);
            color: var(--text-main);
            height: 100vh;
            display: flex;
            overflow: hidden;
        }

        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
            vertical-align: middle;
        }

        /* --- LAYOUT --- */
        .app-body {
            display: flex;
            flex: 1;
            width: 100%;
        }

        /* --- SIDEBAR --- */
        .sidebar {
            width: var(--sidebar-width);
            background-color: #ffffff;
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            padding: 1.5rem;
            flex-shrink: 0;
            transition: all 0.3s ease;
            z-index: 1000;
        }

        .logo-container {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            margin-bottom: 2.5rem;
        }

        .logo-icon {
            background-color: var(--primary);
            color: white;
            padding: 0.5rem;
            border-radius: 0.5rem;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
        }

        .logo-title {
            font-weight: 800;
            font-size: 1.125rem;
            line-height: 1.2;
        }

        .logo-subtitle {
            font-size: 0.75rem;
            color: var(--text-muted);
        }

        .nav-links {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
            flex-grow: 1;
        }

        .nav-item {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.75rem 1rem;
            border-radius: 0.5rem;
            text-decoration: none;
            color: var(--text-muted);
            font-weight: 500;
            transition: all 0.2s;
        }

        .nav-item:hover,
        .nav-item.active {
            background-color: var(--primary-light);
            color: var(--primary);
        }

        .nav-item svg {
            fill: currentColor;
        }

        .sidebar-footer {
            padding-top: 1.5rem;
            border-top: 1px solid var(--border-color);
        }

        .profile-section {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background-color: #cbd5e1;
        }

        .profile-name {
            font-weight: 700;
            font-size: 0.875rem;
        }

        .profile-role {
            font-size: 0.75rem;
            color: var(--text-muted);
        }


        /* --- MAIN CONTENT --- */
        main {
            flex: 1;
            padding: 2rem;
            overflow-y: auto;
            background: rgba(248, 250, 252, 0.5);
        }

        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            margin-bottom: 2rem;
        }

        .btn-primary {
            background: var(--primary);
            color: white;
            padding: 0.625rem 1.25rem;
            border-radius: 8px;
            border: none;
            font-weight: 700;
            font-size: 0.875rem;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            box-shadow: 0 4px 12px rgba(30, 64, 175, 0.2);
        }

        /* --- FILTERS --- */
        .filter-bar {
            background: var(--card-bg);
            padding: 1rem;
            border: 1px solid var(--border-color);
            border-radius: 12px;
            display: flex;
            align-items: center;
            gap: 1.5rem;
            margin-bottom: 1.5rem;
        }

        .filter-group {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .filter-group label {
            font-size: 10px;
            font-weight: 800;
            text-transform: uppercase;
            color: var(--text-light);
            letter-spacing: 0.1em;
        }

        .filter-input {
            background: var(--background);
            border: 1px solid var(--border-color);
            padding: 0.4rem 0.75rem;
            border-radius: 8px;
            font-size: 0.875rem;
            outline: none;
            color: var(--text-main);
        }

        /* --- TABLE --- */
        .table-container {
            background: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            text-align: left;
            padding: 1rem 1.5rem;
            background: #fafafa;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            color: var(--text-light);
            letter-spacing: 0.05em;
            border-bottom: 1px solid var(--border-color);
        }

        td {
            padding: 1rem 1.5rem;
            border-bottom: 1px solid var(--border-color);
            font-size: 0.875rem;
        }

        tr:last-child td {
            border-bottom: none;
        }

        tr:hover {
            background: rgba(0, 0, 0, 0.01);
        }

        .status-badge {
            padding: 0.2rem 0.5rem;
            border-radius: 4px;
            font-size: 10px;
            font-weight: 800;
            text-transform: uppercase;
        }

        .status-active {
            background: var(--active-bg);
            color: var(--active-text);
        }

        .status-inactive {
            background: var(--inactive-bg);
            color: var(--text-muted);
            border: 1px solid var(--border-color);
        }

        /* --- STATE CLASSES --- */
        .sidebar.collapsed {
            width: 80px;
            padding: 1.5rem 0.5rem;
        }

        .sidebar.collapsed .nav-text,
        .sidebar.collapsed .logo-title,
        .sidebar.collapsed .logo-subtitle,
        .sidebar.collapsed .profile-section div {
            display: none;
        }

        .sidebar.collapsed .nav-item,
        .sidebar.collapsed .logo-container {
            justify-content: center;
        }

        .mobile-nav-toggle {
            display: none;
            padding: 1rem;
            background: white;
            border-bottom: 1px solid var(--border-color);
            align-items: center;
        }

        .sidebar-overlay {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0, 0, 0, 0.5);
            z-index: 999;
        }

        /* --- RESPONSIVE --- */
        @media (max-width: 1024px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .dashboard-main-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .mobile-nav-toggle {
                display: flex;
            }

            .sidebar {
                position: fixed;
                left: -100%;
                top: 0;
                bottom: 0;
                width: 280px;
            }

            .sidebar.active {
                left: 0;
            }

            .sidebar-overlay.active {
                display: block;
            }

            .stats-grid {
                grid-template-columns: 1fr;
            }

            .data-table {
                display: block;
                overflow-x: auto;
            }
        }
    </style>
</head>

<body>
    <div class="app-body">
        <div class="mobile-nav-toggle">
            <button id="mobileMenuOpen" style="background:none; border:none; cursor:pointer;">
                <span class="material-symbols-outlined">menu</span>
            </button>
            <div style="margin-left: 1rem; font-weight: 800;">MediCenter</div>
        </div>

        <aside class="sidebar">
            <div class="logo-container">
                <div class="logo-icon" id="sidebarToggle">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#ffffff">
                        <path d="M160-80q-33 0-56.5-23.5T80-160v-480q0-33 23.5-56.5T160-720h160v-80q0-33 23.5-56.5T400-880h160q33 0 56.5 23.5T640-800v80h160q33 0 56.5 23.5T880-640v480q0 33-23.5 56.5T800-80H160Zm0-80h640v-480H160v480Zm240-560h160v-80H400v80ZM160-160v-480 480Zm280-200v120h80v-120h120v-80H520v-120h-80v120H320v80h120Z" />
                    </svg>
                </div>
                <div>
                    <div class="logo-title">MediCenter</div>
                    <div class="logo-subtitle">Clinic Management</div>
                </div>
            </div>

            <nav class="nav-links">
                <a class="nav-item" href="index.php">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="var(--text-muted)">
                        <path d="M520-600v-240h320v240H520ZM120-440v-400h320v400H120Zm400 320v-400h320v400H520Zm-400 0v-240h320v240H120Zm80-400h160v-240H200v240Zm400 320h160v-240H600v240Zm0-480h160v-80H600v80ZM200-200h160v-80H200v80Zm160-320Zm240-160Zm0 240ZM360-280Z" />
                    </svg>
                    <span class="nav-text">Dashboard</span>
                </a>
                <a class="nav-item" href="appointments.php">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="var(--text-muted)">
                        <path d="M200-80q-33 0-56.5-23.5T120-160v-560q0-33 23.5-56.5T200-800h40v-80h80v80h320v-80h80v80h40q33 0 56.5 23.5T840-720v560q0 33-23.5 56.5T760-80H200Zm0-80h560v-400H200v400Zm0-480h560v-80H200v80Zm0 0v-80 80Zm280 240q-17 0-28.5-11.5T440-440q0-17 11.5-28.5T480-480q17 0 28.5 11.5T520-440q0 17-11.5 28.5T480-400Zm-188.5-11.5Q280-423 280-440t11.5-28.5Q303-480 320-480t28.5 11.5Q360-457 360-440t-11.5 28.5Q337-400 320-400t-28.5-11.5ZM640-400q-17 0-28.5-11.5T600-440q0-17 11.5-28.5T640-480q17 0 28.5 11.5T680-440q0 17-11.5 28.5T640-400ZM480-240q-17 0-28.5-11.5T440-280q0-17 11.5-28.5T480-320q17 0 28.5 11.5T520-280q0 17-11.5 28.5T480-240Zm-188.5-11.5Q280-263 280-280t11.5-28.5Q303-320 320-320t28.5 11.5Q360-297 360-280t-11.5 28.5Q337-240 320-240t-28.5-11.5ZM640-240q-17 0-28.5-11.5T600-280q0-17 11.5-28.5T640-320q17 0 28.5 11.5T680-280q0 17-11.5 28.5T640-240Z" />
                    </svg>
                    <span class="nav-text">Appointments</span>
                </a>
                <a class="nav-item active" href="patient.php">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="var(--text-muted)">
                        <path d="M367-527q-47-47-47-113t47-113q47-47 113-47t113 47q47 47 47 113t-47 113q-47 47-113 47t-113-47ZM160-160v-112q0-34 17.5-62.5T224-378q62-31 126-46.5T480-440q66 0 130 15.5T736-378q29 15 46.5 43.5T800-272v112H160Zm80-80h480v-32q0-11-5.5-20T700-306q-54-27-109-40.5T480-360q-56 0-111 13.5T260-306q-9 5-14.5 14t-5.5 20v32Zm296.5-343.5Q560-607 560-640t-23.5-56.5Q513-720 480-720t-56.5 23.5Q400-673 400-640t23.5 56.5Q447-560 480-560t56.5-23.5ZM480-640Zm0 400Z" />
                    </svg>
                    <span class="nav-text">Patients</span>
                </a>
                <a class="nav-item" href="staff.php">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="var(--text-muted)">
                        <path d="M40-160v-112q0-34 17.5-62.5T104-378q62-31 126-46.5T360-440q66 0 130 15.5T616-378q29 15 46.5 43.5T680-272v112H40Zm720 0v-120q0-44-24.5-84.5T666-434q51 6 96 20.5t84 35.5q36 20 55 44.5t19 53.5v120H760ZM247-527q-47-47-47-113t47-113q47-47 113-47t113 47q47 47 47 113t-47 113q-47 47-113 47t-113-47Zm466 0q-47 47-113 47-11 0-28-2.5t-28-5.5q27-32 41.5-71t14.5-81q0-42-14.5-81T544-792q14-5 28-6.5t28-1.5q66 0 113 47t47 113q0 66-47 113ZM120-240h480v-32q0-11-5.5-20T580-306q-54-27-109-40.5T360-360q-56 0-111 13.5T140-306q-9 5-14.5 14t-5.5 20v32Zm296.5-343.5Q440-607 440-640t-23.5-56.5Q393-720 360-720t-56.5 23.5Q280-673 280-640t23.5 56.5Q327-560 360-560t56.5-23.5ZM360-240Zm0-400Z" />
                    </svg>
                    <span class="nav-text">Staff</span>
                </a>
                <a class="nav-item" href="billing.php">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="var(--text-muted)">
                        <path d="M240-80q-50 0-85-35t-35-85v-120h120v-560l60 60 60-60 60 60 60-60 60 60 60-60 60 60 60-60 60 60 60-60v680q0 50-35 85t-85 35H240Zm480-80q17 0 28.5-11.5T760-200v-560H320v440h360v120q0 17 11.5 28.5T720-160ZM360-600v-80h240v80H360Zm0 120v-80h240v80H360Zm320-120q-17 0-28.5-11.5T640-640q0-17 11.5-28.5T680-680q17 0 28.5 11.5T720-640q0 17-11.5 28.5T680-600Zm0 120q-17 0-28.5-11.5T640-520q0-17 11.5-28.5T680-560q17 0 28.5 11.5T720-520q0 17-11.5 28.5T680-480ZM240-160h360v-80H200v40q0 17 11.5 28.5T240-160Zm-40 0v-80 80Z" />
                    </svg>
                    <span class="nav-text">Billing</span>
                </a>
                <a class="nav-item" href="feedback.php">
                    <svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="var(--text-muted)">
                        <path d="M480-360q17 0 28.5-11.5T520-400q0-17-11.5-28.5T480-440q-17 0-28.5 11.5T440-400q0 17 11.5 28.5T480-360Zm-40-160h80v-240h-80v240ZM80-80v-720q0-33 23.5-56.5T160-880h640q33 0 56.5 23.5T880-800v480q0 33-23.5 56.5T800-240H240L80-80Zm126-240h594v-480H160v525l46-45Zm-46 0v-480 480Z" />
                    </svg>
                    <span class="nav-text">Feedback</span>
                </a>
            </nav>

            <div class="sidebar-footer">
                <div class="profile-section">
                    <img alt="Dr. Smith" class="avatar" src="https://ui-avatars.com/api/?name=James+Smith&background=cbd5e1" />
                    <div>
                        <div class="profile-name">Dr. James Smith</div>
                        <div class="profile-role">Senior Surgeon</div>
                    </div>
                </div>
            </div>
        </aside>

        <script>
            const sidebar = document.querySelector('.sidebar');
            const overlay = document.getElementById('overlay');

            document.getElementById('sidebarToggle').addEventListener('click', () => {
                sidebar.classList.toggle('collapsed');
            });

            document.getElementById('mobileMenuOpen').addEventListener('click', () => {
                sidebar.classList.add('active');
                overlay.classList.add('active');
            });

            overlay.addEventListener('click', () => {
                sidebar.classList.remove('active');
                overlay.classList.remove('active');
            });
        </script>

        <main>
            <div class="page-header">
                <div>
                    <nav style="font-size: 0.75rem; color: var(--text-light); margin-bottom: 0.5rem; font-weight: 600;">
                        ClinicConnect <span class="material-symbols-outlined" style="font-size: 12px;">chevron_right</span> Patients
                    </nav>
                    <h2 style="font-size: 1.8rem; font-weight: 900; letter-spacing: -0.03em;">Patient Directory</h2>
                    <p style="color: var(--text-muted); font-size: 0.875rem;">Access and manage medical records.</p>
                </div>
                <button class="btn-primary"><span class="material-symbols-outlined">person_add</span> Register Patient</button>
            </div>

            <div class="filter-bar">
                <div class="filter-group">
                    <label>Search</label>
                    <input type="text" class="filter-input" placeholder="Name or ID..." style="width: 180px;">
                </div>
                <div class="filter-group">
                    <label>Status</label>
                    <select class="filter-input">
                        <option>All Patients</option>
                        <option>Active</option>
                        <option>Inactive</option>
                    </select>
                </div>
                <div style="margin-left: auto; display: flex; gap: 0.5rem;">
                    <button class="filter-input" style="background:none; font-weight:700; cursor:pointer;">Filters</button>
                    <button class="filter-input" style="background:none; cursor:pointer;"><span class="material-symbols-outlined" style="font-size:1.2rem;">download</span></button>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Patient ID</th>
                            <th>Full Name</th>
                            <th>Age / Gender</th>
                            <th>Registration Date</th>
                            <th>Primary Doctor</th>
                            <th>Status</th>
                            <th style="text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                                    <td><span style='font-family: monospace; font-weight: 700; background: var(--background); padding: 2px 6px; border-radius: 4px;'>1002</span></td>
                                    <td>
                                        <div style='display: flex; align-items: center; gap: 0.75rem;'>
                                            <img src='https://images.unsplash.com/photo-1438761681033-6461ffad8d80' style='width: 32px; height: 32px; border-radius: 50%;'>
                                            <div>
                                                <p style='font-weight: 700;'>Chenitha</p>
                                                <p style='font-size: 10px; color: var(--text-light);'>chenitha@example.com</p>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <p style='font-weight: 600;'>20 Yrs</p>
                                        <p style='font-size: 10px; color: var(--text-light); text-transform: uppercase; font-weight: 800;'>male</p>
                                    </td>
                                    <td>2024-12-01</td>
                                    <td style='font-weight: 500;'>Dr.Nimal</td>
                                    <td><span class='status-badge active'>Active</span></td>
                                    <td style='text-align: right;'>
                                        <span class='material-symbols-outlined' style='color: var(--text-light); cursor: pointer; margin-left: 10px;'>visibility</span>
                                        <span class='material-symbols-outlined' style='color: var(--text-light); cursor: pointer; margin-left: 10px;'>edit</span>
                                    </td>
                                </tr>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
</body>

</html>