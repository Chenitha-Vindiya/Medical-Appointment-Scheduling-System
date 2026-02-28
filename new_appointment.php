<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <title>Ongoing Appointments | HealthCenter</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap"
        rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
        rel="stylesheet" />

    <style>
        /* --- DESIGN SYSTEM --- */
        :root {
            --primary: #13a4ec;
            --primary-soft: rgba(19, 164, 236, 0.1);
            --bg-body: #f6f7f8;
            --bg-card: #ffffff;
            --border: #e2e8f0;
            --text-main: #0f172a;
            --text-muted: #64748b;
            --text-light: #94a3b8;

            /* Status Colors */
            --ongoing-bg: #dcfce7;
            --ongoing-text: #15803d;
            --waiting-bg: #fef3c7;
            --waiting-text: #b45309;
        }

        /* @media (prefers-color-scheme: dark) {
            :root {
                --bg-body: #101c22;
                --bg-card: #0f172a;
                --border: #1e293b;
                --text-main: #f8fafc;
                --text-muted: #94a3b8;
                --ongoing-bg: rgba(21, 128, 61, 0.2);
                --ongoing-text: #4ade80;
                --waiting-bg: rgba(180, 83, 9, 0.2);
                --waiting-text: #fbbf24;
            }
        } */

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg-body);
            color: var(--text-main);
            min-height: 100vh;
            -webkit-font-smoothing: antialiased;
        }

        .material-symbols-outlined {
            font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
            vertical-align: middle;
        }

        /* --- LAYOUT --- */
        .dashboard-wrapper {
            display: flex;
        }

        aside {
            width: 256px;
            background: var(--bg-card);
            border-right: 1px solid var(--border);
            height: 100vh;
            position: fixed;
            display: flex;
            flex-direction: column;
            z-index: 40;
        }

        main {
            flex: 1;
            margin-left: 256px;
            padding: 2rem;
        }

        /* --- SIDEBAR --- */
        .brand {
            padding: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }

        .brand-icon {
            width: 40px;
            height: 40px;
            background: var(--primary);
            color: white;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .nav-link {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 0.75rem 1rem;
            margin: 0 1rem 0.25rem;
            text-decoration: none;
            color: var(--text-muted);
            font-size: 0.875rem;
            font-weight: 500;
            border-radius: 8px;
            transition: 0.2s;
        }

        .nav-link:hover {
            background: rgba(0, 0, 0, 0.03);
        }

        .nav-link.active {
            background: var(--primary-soft);
            color: var(--primary);
            font-weight: 600;
        }

        /* --- HEADER & TABS --- */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            margin-bottom: 2rem;
        }

        .live-indicator {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: var(--primary);
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.1em;
            margin-bottom: 0.5rem;
        }

        .dot-pulse {
            width: 8px;
            height: 8px;
            background: var(--primary);
            border-radius: 50%;
            animation: pulse 1.5s infinite;
        }

        @keyframes pulse {
            0% {
                transform: scale(0.95);
                box-shadow: 0 0 0 0 rgba(19, 164, 236, 0.7);
            }

            70% {
                transform: scale(1);
                box-shadow: 0 0 0 10px rgba(19, 164, 236, 0);
            }

            100% {
                transform: scale(0.95);
                box-shadow: 0 0 0 0 rgba(19, 164, 236, 0);
            }
        }

        .tabs {
            display: flex;
            gap: 2rem;
            border-bottom: 1px solid var(--border);
            margin-bottom: 1.5rem;
        }

        .tab-item {
            padding-bottom: 1rem;
            text-decoration: none;
            color: var(--text-muted);
            font-size: 0.875rem;
            font-weight: 700;
            border-bottom: 2px solid transparent;
        }

        .tab-item.active {
            color: var(--primary);
            border-color: var(--primary);
        }

        .tab-count {
            font-size: 10px;
            background: var(--primary-soft);
            padding: 2px 6px;
            border-radius: 4px;
            margin-left: 4px;
        }

        /* --- DATA TABLE --- */
        .card {
            background: var(--bg-card);
            border: 1px solid var(--border);
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        th {
            background: rgba(0, 0, 0, 0.01);
            padding: 1rem 1.5rem;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
            color: var(--text-light);
            letter-spacing: 0.05em;
            border-bottom: 1px solid var(--border);
        }

        td {
            padding: 1rem 1.5rem;
            border-bottom: 1px solid var(--border);
            font-size: 0.875rem;
        }

        tr:hover {
            background: rgba(0, 0, 0, 0.01);
        }

        .patient-avatar {
            width: 36px;
            height: 36px;
            background: var(--primary-soft);
            color: var(--primary);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 0.75rem;
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.25rem 0.75rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 700;
        }

        .ongoing {
            background: var(--ongoing-bg);
            color: var(--ongoing-text);
        }

        .waiting {
            background: var(--waiting-bg);
            color: var(--waiting-text);
        }

        /* --- STATS GRID --- */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 1.5rem;
            margin-top: 2rem;
        }

        .stat-card {
            padding: 1.5rem;
            border-radius: 12px;
            border: 1px solid var(--border);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .stat-label {
            font-size: 10px;
            font-weight: 800;
            color: var(--text-light);
            text-transform: uppercase;
            margin-bottom: 0.5rem;
        }

        .stat-value {
            font-size: 1.5rem;
            font-weight: 900;
        }
    </style>
</head>

<body>

    <div class="dashboard-wrapper">
        <aside>
            <div class="brand">
                <div class="brand-icon">
                    <span class="material-symbols-outlined">medical_services</span>
                </div>
                <div>
                    <h2 style="font-size: 1rem; font-weight: 900; line-height: 1;">HealthCenter</h2>
                    <p style="font-size: 10px; color: var(--text-light); text-transform: uppercase; font-weight: 800;">
                        Hospital Admin</p>
                </div>
            </div>

            <nav style="flex: 1; margin-top: 1rem;">
                <a href="#" class="nav-link"><span class="material-symbols-outlined">dashboard</span>Dashboard</a>
                <a href="#" class="nav-link active"><span
                        class="material-symbols-outlined">calendar_today</span>Appointments</a>
                <a href="#" class="nav-link"><span class="material-symbols-outlined">group</span>Patients</a>
                <a href="#" class="nav-link"><span class="material-symbols-outlined">stethoscope</span>Doctors</a>
                <a href="#" class="nav-link"><span class="material-symbols-outlined">analytics</span>Reports</a>
            </nav>

            <div style="padding: 1rem; border-top: 1px solid var(--border);">
                <a href="#" class="nav-link" style="margin: 0 0 1rem 0;"><span
                        class="material-symbols-outlined">settings</span>Settings</a>
                <button
                    style="width: 100%; background: var(--primary); color: white; border: none; padding: 0.75rem; border-radius: 8px; font-weight: 700; display: flex; align-items: center; justify-content: center; gap: 0.5rem; cursor: pointer;">
                    <span class="material-symbols-outlined">add</span> Schedule New
                </button>
            </div>
        </aside>

        <main>
            <header class="page-header">
                <div>
                    <div class="live-indicator">
                        <div class="dot-pulse"></div>
                        Live Dashboard
                    </div>
                    <h1 style="font-size: 2rem; font-weight: 900; letter-spacing: -0.02em;">Ongoing Appointments</h1>
                    <p style="color: var(--text-muted); font-size: 1.1rem; margin-top: 0.25rem;">Real-time view of
                        current clinical activities.</p>
                </div>
                <div style="display: flex; gap: 0.75rem;">
                    <button
                        style="background: var(--bg-card); border: 1px solid var(--border); padding: 0.5rem 1rem; border-radius: 8px; font-weight: 700; display: flex; align-items: center; gap: 0.5rem; cursor: pointer; color: var(--text-main);">
                        <span class="material-symbols-outlined" style="font-size: 18px;">refresh</span> Refresh
                    </button>
                    <button
                        style="background: #0f172a; color: white; border: none; padding: 0.5rem 1rem; border-radius: 8px; font-weight: 700; display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                        <span class="material-symbols-outlined" style="font-size: 18px;">filter_list</span> Filters
                    </button>
                </div>
            </header>

            <div class="tabs">
                <a href="#" class="tab-item">All Appointments</a>
                <a href="#" class="tab-item active">Ongoing <span class="tab-count">12</span></a>
                <a href="#" class="tab-item">Waiting <span class="tab-count" style="background: #f1f5f9;">8</span></a>
                <a href="#" class="tab-item">Emergency</a>
            </div>

            <div class="card">
                <table>
                    <thead>
                        <tr>
                            <th>Patient Name</th>
                            <th>Assigned Doctor</th>
                            <th>Specialty</th>
                            <th>Room #</th>
                            <th>Status</th>
                            <th>Duration</th>
                            <th style="text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <div style="display: flex; align-items: center; gap: 0.75rem;">
                                    <div class="patient-avatar">JD</div>
                                    <span style="font-weight: 600;">John Doe</span>
                                </div>
                            </td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                    <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuDRZ07F9lFa4duZDoOuG19M-6K5d-pKSP87xnveXaMIJIqJot-5BP5_aq7VEeeeD0rL54dnw57DCyJ5v-eJLy0enuckG3XNQvZrY4_hiqiuumqV4-yBiwLQxltuAPkZnLCitxthXHuYF3obEf8Gs9DrWcWzpNdrfaX2p5GJBrV7B7QExJA2PQZ-nPvCRB_RyNDxWpD5oeF3QAv76UaC2s6WCO7SVmi2lf_RyvNgPoZk7HeNAmXM5-SagNlifPD6tp5zkG61_Q2fsgrF"
                                        style="width: 24px; height: 24px; border-radius: 50%;">
                                    <span style="font-weight: 500;">Dr. Smith</span>
                                </div>
                            </td>
                            <td><span
                                    style="background: var(--bg-body); border: 1px solid var(--border); padding: 2px 8px; border-radius: 9999px; font-size: 11px;">Cardiology</span>
                            </td>
                            <td style="font-family: monospace; font-weight: 600;">302-A</td>
                            <td>
                                <span class="status-badge ongoing">
                                    <span
                                        style="width: 6px; height: 6px; background: #22c55e; border-radius: 50%;"></span>
                                    Ongoing
                                </span>
                            </td>
                            <td style="font-weight: 500;">15m 45s</td>
                            <td style="text-align: right;">
                                <button
                                    style="color: var(--primary); background: none; border: none; font-weight: 700; font-size: 0.75rem; cursor: pointer; margin-right: 1rem;">View
                                    Record</button>
                                <span class="material-symbols-outlined"
                                    style="color: var(--text-light); cursor: pointer;">check_circle</span>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div style="display: flex; align-items: center; gap: 0.75rem;">
                                    <div class="patient-avatar">JR</div>
                                    <span style="font-weight: 600;">Jane Roe</span>
                                </div>
                            </td>
                            <td>
                                <div style="display: flex; align-items: center; gap: 0.5rem;">
                                    <img src="https://lh3.googleusercontent.com/aida-public/AB6AXuDqbiK4IsQIgZTB0At4BReN_e50lCKFObnV_n4v-EWeZ-Dyi8pxtfS0zZYWnIUIYZrZfwXQKfRa9cLrL3qw8GeBlGrW4j1n0vi-Y09fHnZPHiP7QR1B6tzO1eHhc-F3NgJVYfi60zDj3sYKDiR6huhGDfGCSUssKDfoeA32Tn0wacwPWjdbhR14GqIuz6tENU5yAJ9iVYVDo4bC-Hv6xTOiIiIj4frqkeH8zfUjBn53MV9vxtDGTLZACknDBzuz-nIILlvu4wJ0fpPY"
                                        style="width: 24px; height: 24px; border-radius: 50%;">
                                    <span style="font-weight: 500;">Dr. Adams</span>
                                </div>
                            </td>
                            <td><span
                                    style="background: var(--bg-body); border: 1px solid var(--border); padding: 2px 8px; border-radius: 9999px; font-size: 11px;">Pediatrics</span>
                            </td>
                            <td style="font-family: monospace; font-weight: 600;">105-C</td>
                            <td>
                                <span class="status-badge waiting">
                                    <span
                                        style="width: 6px; height: 6px; background: #f59e0b; border-radius: 50%;"></span>
                                    Waiting
                                </span>
                            </td>
                            <td style="font-weight: 500;">45m 12s</td>
                            <td style="text-align: right;">
                                <button
                                    style="color: var(--primary); background: none; border: none; font-weight: 700; font-size: 0.75rem; cursor: pointer; margin-right: 1rem;">View
                                    Record</button>
                                <span class="material-symbols-outlined"
                                    style="color: var(--text-light); cursor: pointer;">check_circle</span>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <div
                    style="padding: 1rem 1.5rem; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border);">
                    <p style="font-size: 0.875rem; color: var(--text-muted);">Showing <b>2</b> of <b>12</b> ongoing</p>
                    <div style="display: flex; gap: 0.25rem;">
                        <button
                            style="width: 32px; height: 32px; border: 1px solid var(--border); background: white; border-radius: 6px;"><span
                                class="material-symbols-outlined" style="font-size: 16px;">chevron_left</span></button>
                        <button
                            style="width: 32px; height: 32px; background: var(--primary); color: white; border: none; border-radius: 6px; font-weight: 700;">1</button>
                        <button
                            style="width: 32px; height: 32px; border: 1px solid var(--border); background: white; border-radius: 6px; font-weight: 700;">2</button>
                        <button
                            style="width: 32px; height: 32px; border: 1px solid var(--border); background: white; border-radius: 6px;"><span
                                class="material-symbols-outlined" style="font-size: 16px;">chevron_right</span></button>
                    </div>
                </div>
            </div>

            <div class="stats-grid">
                <div class="stat-card" style="background: var(--primary-soft); border-color: rgba(19, 164, 236, 0.2);">
                    <div>
                        <p class="stat-label" style="color: var(--primary);">Total Rooms</p>
                        <p class="stat-value">24 / 30</p>
                    </div>
                    <span class="material-symbols-outlined"
                        style="font-size: 2.5rem; color: var(--primary);">meeting_room</span>
                </div>
                <div class="stat-card" style="background: var(--bg-card);">
                    <div>
                        <p class="stat-label">Avg. Wait Time</p>
                        <p class="stat-value">14.2 min</p>
                    </div>
                    <span class="material-symbols-outlined"
                        style="font-size: 2.5rem; color: var(--text-light);">timer</span>
                </div>
                <div class="stat-card" style="background: var(--bg-card);">
                    <div>
                        <p class="stat-label">Today's Load</p>
                        <p class="stat-value">142 Visits</p>
                    </div>
                    <span class="material-symbols-outlined"
                        style="font-size: 2.5rem; color: var(--text-light);">trending_up</span>
                </div>
            </div>
        </main>
    </div>

</body>

</html>