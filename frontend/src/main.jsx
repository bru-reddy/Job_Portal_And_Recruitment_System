import React from "react";
import { createRoot } from "react-dom/client";
import { BriefcaseBusiness, Building2, FileCheck2, Search, ShieldCheck, Sparkles, Users } from "lucide-react";
import "./styles.css";

const jobs = [
  { title: "Java Backend Developer", company: "Nova Systems", location: "Hyderabad", type: "Full-time", level: "Mid-level", posted: "2 days ago" },
  { title: "Frontend Engineer", company: "BluePeak Labs", location: "Bengaluru", type: "Full-time", level: "Entry-level", posted: "1 day ago" },
  { title: "Full Stack Developer", company: "OrbitWorks", location: "Remote", type: "Full-time", level: "Junior", posted: "4 days ago" }
];

function App() {
  return (
    <div className="app-shell">
      <header className="navbar">
        <div className="brand">
          <div className="brand-mark"><BriefcaseBusiness size={20} /></div>
          <span>JobSphere</span>
        </div>
        <nav>
          <a href="#jobs">Find Jobs</a>
          <a href="#companies">Companies</a>
          <a href="#how">How It Works</a>
        </nav>
        <div className="nav-actions">
          <button className="btn ghost">Sign in</button>
          <button className="btn primary">Post a Job</button>
        </div>
      </header>

      <main>
        <section className="hero">
          <div className="hero-copy">
            <div className="eyebrow"><Sparkles size={15} /> Smarter hiring, better careers</div>
            <h1>Find the right opportunity. <span>Build what’s next.</span></h1>
            <p>One professional platform for candidates, recruiters, and teams to discover talent and manage recruitment from application to hiring.</p>
            <div className="search-panel">
              <div className="search-field"><Search size={18} /><input placeholder="Job title, skill or keyword" /></div>
              <div className="search-field"><Building2 size={18} /><input placeholder="Location" /></div>
              <button className="btn primary search-btn">Search jobs</button>
            </div>
            <div className="hero-meta">
              <span>Popular:</span> Java <span>•</span> Spring Boot <span>•</span> React <span>•</span> Data Analyst
            </div>
          </div>
          <div className="hero-panel">
            <div className="dashboard-card">
              <div className="card-head"><span>Recruitment overview</span><span className="live-dot">● Live</span></div>
              <div className="metric-row">
                <div><strong>1,248</strong><small>Active jobs</small></div>
                <div><strong>8,620</strong><small>Candidates</small></div>
              </div>
              <div className="mini-chart">
                <span style={{height:"42%"}}></span><span style={{height:"55%"}}></span><span style={{height:"48%"}}></span><span style={{height:"72%"}}></span><span style={{height:"65%"}}></span><span style={{height:"88%"}}></span><span style={{height:"78%"}}></span>
              </div>
              <div className="activity"><div className="avatar">JD</div><div><b>Java Developer</b><small>Application moved to interview</small></div><span>Now</span></div>
              <div className="activity"><div className="avatar alt">AS</div><div><b>Frontend Engineer</b><small>Profile shortlisted</small></div><span>8m</span></div>
            </div>
          </div>
        </section>

        <section className="stats">
          <div><b>1,248+</b><span>Open positions</span></div>
          <div><b>8.6K+</b><span>Registered candidates</span></div>
          <div><b>420+</b><span>Hiring companies</span></div>
          <div><b>95%</b><span>Application visibility</span></div>
        </section>

        <section id="jobs" className="section">
          <div className="section-head"><div><div className="kicker">OPPORTUNITIES</div><h2>Latest job openings</h2></div><button className="text-btn">View all jobs →</button></div>
          <div className="job-grid">
            {jobs.map((job) => <article className="job-card" key={job.title}><div className="company-logo">{job.company[0]}</div><div className="job-top"><span className="badge">{job.type}</span><span className="posted">{job.posted}</span></div><h3>{job.title}</h3><p className="company">{job.company}</p><p className="location">⌖ {job.location}</p><div className="card-footer"><span>{job.level}</span><button className="apply">View role</button></div></article>)}
          </div>
        </section>

        <section id="how" className="section workflow"><div className="kicker">ONE PLATFORM, THREE EXPERIENCES</div><h2>Designed around every side of hiring</h2><div className="role-grid"><div className="role-card"><div className="role-icon"><Users /></div><h3>For candidates</h3><p>Create a strong profile, upload your resume, discover relevant roles, apply online, and track every application.</p></div><div className="role-card"><div className="role-icon"><BriefcaseBusiness /></div><h3>For recruiters</h3><p>Create company listings, publish vacancies, review candidate profiles, shortlist applicants, and manage your hiring pipeline.</p></div><div className="role-card"><div className="role-icon"><ShieldCheck /></div><h3>For administrators</h3><p>Control platform access, manage users and listings, and maintain reliable recruitment operations.</p></div></div></section>

        <section className="cta"><div><div className="kicker light">START BUILDING</div><h2>Make every hire more structured.</h2><p>A clean foundation for a scalable recruitment application.</p></div><button className="btn white">Explore the platform</button></section>
      </main>

      <footer><div className="brand"><div className="brand-mark"><BriefcaseBusiness size={18} /></div><span>JobSphere</span></div><span>Java Full Stack Job Portal & Recruitment System</span></footer>
    </div>
  );
}
createRoot(document.getElementById("root")).render(<React.StrictMode><App /></React.StrictMode>);
