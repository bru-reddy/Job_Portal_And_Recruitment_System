import React, { useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import {
  Activity, Bell, BriefcaseBusiness, Building2, CheckCircle2, ChevronRight, Clock3,
  FileText, Filter, Heart, LayoutDashboard, LogIn, Menu, Search, ShieldCheck,
  Sparkles, UserRound, Users, X
} from "lucide-react";
import "./styles.css";
import { api } from "./api";

const jobs = [
  { title:"Java Backend Developer", company:"Nova Systems", location:"Hyderabad", type:"Full-time", level:"Mid-level", salary:"₹8–12 LPA", posted:"2 days ago", tags:["Java","Spring Boot","SQL"] },
  { title:"Frontend Engineer", company:"BluePeak Labs", location:"Bengaluru", type:"Full-time", level:"Entry-level", salary:"₹5–8 LPA", posted:"1 day ago", tags:["React","JavaScript","CSS"] },
  { title:"Full Stack Developer", company:"OrbitWorks", location:"Remote", type:"Full-time", level:"Junior", salary:"₹6–10 LPA", posted:"4 days ago", tags:["Java","React","MySQL"] },
  { title:"Data Analyst", company:"Vertex Analytics", location:"Hyderabad", type:"Full-time", level:"Junior", salary:"₹4–7 LPA", posted:"5 days ago", tags:["Python","SQL","Excel"] },
  { title:"Spring Boot Developer", company:"CloudNova", location:"Pune", type:"Hybrid", level:"Mid-level", salary:"₹9–14 LPA", posted:"6 days ago", tags:["Java","Spring","AWS"] },
  { title:"Software Engineer", company:"Nexora Technologies", location:"Chennai", type:"Full-time", level:"Fresher", salary:"₹4–6 LPA", posted:"1 week ago", tags:["Java","DSA","Git"] }
];

function App(){
  const [screen,setScreen]=useState("home");
  const [menu,setMenu]=useState(false);
  const [query,setQuery]=useState("");
  const [location,setLocation]=useState("");
  const [saved,setSaved]=useState([]);
  const [toast,setToast]=useState("");
  const [role,setRole]=useState("candidate");

  const filtered=useMemo(()=>jobs.filter(j=>
    (!query || (j.title+j.company+j.tags.join(" ")).toLowerCase().includes(query.toLowerCase())) &&
    (!location || j.location.toLowerCase().includes(location.toLowerCase()) || location.toLowerCase()==="remote" && j.location==="Remote")
  ),[query,location]);

  const notify=(msg)=>{setToast(msg);setTimeout(()=>setToast(""),2600)};
  const go=(s)=>{setScreen(s);setMenu(false);window.scrollTo({top:0,behavior:"smooth"})};

  return <div className="app-shell">
    <header className="navbar">
      <button className="brand brand-button" onClick={()=>go("home")}><span className="brand-mark"><BriefcaseBusiness size={19}/></span><span>JobSphere</span></button>
      <nav>
        <button onClick={()=>go("jobs")}>Find Jobs</button><button onClick={()=>go("companies")}>Companies</button><button onClick={()=>go("dashboard")}>Dashboard</button><button onClick={()=>go("how")}>How It Works</button>
      </nav>
      <div className="nav-actions"><button className="btn ghost" onClick={()=>go("login")}><LogIn size={15}/> Sign in</button><button className="btn primary" onClick={()=>{setRole("recruiter");go("post")}}>Post a Job</button></div>
      <button className="mobile-menu" onClick={()=>setMenu(!menu)}>{menu?<X/>:<Menu/>}</button>
    </header>
    {menu&&<div className="mobile-nav"><button onClick={()=>go("jobs")}>Find Jobs</button><button onClick={()=>go("companies")}>Companies</button><button onClick={()=>go("dashboard")}>Dashboard</button><button onClick={()=>go("login")}>Sign in</button></div>}

    {screen==="home"&&<main>
      <section className="hero"><div className="hero-copy">
        <div className="eyebrow"><Sparkles size={15}/> SMARTER HIRING, BETTER CAREERS</div>
        <h1>Find the right opportunity. <span>Build what’s next.</span></h1>
        <p>One professional platform for candidates, recruiters, and teams to discover talent and manage recruitment from application to hiring.</p>
        <SearchBox query={query} setQuery={setQuery} location={location} setLocation={setLocation} onSearch={()=>go("jobs")}/>
        <div className="hero-meta"><b>Popular:</b> Java • Spring Boot • React • Data Analyst • Python</div>
      </div><DashboardPreview/></section>
      <Stats/>
      <JobsSection jobs={jobs.slice(0,3)} onView={()=>go("jobs")} saved={saved} setSaved={setSaved} notify={notify}/>
      <Companies onView={()=>go("companies")}/>
      <section id="how" className="section workflow"><div className="kicker">ONE PLATFORM, THREE EXPERIENCES</div><h2>Designed around every side of hiring</h2><div className="role-grid">
        <Role icon={<Users/>} title="For candidates" text="Build your profile, upload your resume, discover relevant roles, apply online, and track every application."/>
        <Role icon={<BriefcaseBusiness/>} title="For recruiters" text="Create company listings, publish vacancies, review candidates, shortlist applicants, and manage your hiring pipeline."/>
        <Role icon={<ShieldCheck/>} title="For administrators" text="Control platform access, manage users and listings, and maintain reliable recruitment operations."/>
      </div></section>
      <CTA onClick={()=>go("register")}/>
    </main>}

    {screen==="jobs"&&<JobsPage jobs={filtered} query={query} setQuery={setQuery} location={location} setLocation={setLocation} saved={saved} setSaved={setSaved} notify={notify}/>}
    {screen==="companies"&&<CompaniesPage onView={(c)=>notify("Company profile: "+c)}/>}
    {screen==="dashboard"&&<Dashboard role={role} go={go} notify={notify}/>}
    {screen==="login"&&<Auth mode="login" setRole={setRole} go={go} notify={notify}/>}
    {screen==="register"&&<Auth mode="register" setRole={setRole} go={go} notify={notify}/>}
    {screen==="post"&&<PostJob go={go} notify={notify}/>}
    {screen==="how"&&<HowPage go={go}/>}

    <footer><div className="brand"><span className="brand-mark"><BriefcaseBusiness size={18}/></span><span>JobSphere</span></div><span>Java Full Stack Job Portal & Recruitment System</span><span>Privacy · Terms · Help</span></footer>
    {toast&&<div className="toast"><CheckCircle2 size={17}/>{toast}</div>}
  </div>
}

function SearchBox({query,setQuery,location,setLocation,onSearch}){return <div className="search-panel"><div className="search-field"><Search size={18}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Job title, skill or keyword"/></div><div className="search-field"><Building2 size={18}/><input value={location} onChange={e=>setLocation(e.target.value)} placeholder="Location"/></div><button className="btn primary search-btn" onClick={onSearch}>Search jobs</button></div>}
function DashboardPreview(){return <div className="hero-panel"><div className="dashboard-card"><div className="card-head"><span>Recruitment overview</span><span className="live-dot">● Live</span></div><div className="metric-row"><div><strong>1,248</strong><small>Active jobs</small></div><div><strong>8,620</strong><small>Candidates</small></div></div><div className="mini-chart">{[42,55,48,72,65,88,78].map((h,i)=><span key={i} style={{height:h+"%"}}/>)}</div><ActivityRow avatar="JD" title="Java Developer" sub="Application moved to interview" time="Now"/><ActivityRow avatar="AS" title="Frontend Engineer" sub="Profile shortlisted" time="8m"/></div></div>}
function ActivityRow({avatar,title,sub,time}){return <div className="activity"><div className="avatar">{avatar}</div><div><b>{title}</b><small>{sub}</small></div><span>{time}</span></div>}
function Stats(){return <section className="stats">{[["1,248+","Open positions"],["8.6K+","Registered candidates"],["420+","Hiring companies"],["95%","Application visibility"]].map(x=><div key={x[1]}><b>{x[0]}</b><span>{x[1]}</span></div>)}</section>}
function JobsSection({jobs,onView,saved,setSaved,notify}){return <section className="section"><div className="section-head"><div><div className="kicker">OPPORTUNITIES</div><h2>Latest job openings</h2></div><button className="text-btn" onClick={onView}>View all jobs <ChevronRight size={15}/></button></div><div className="job-grid">{jobs.map(j=><JobCard key={j.title} job={j} saved={saved.includes(j.title)} toggle={()=>setSaved(saved.includes(j.title)?saved.filter(x=>x!==j.title):[...saved,j.title])} notify={notify}/>)}</div></section>}
function JobCard({job,saved,toggle,notify}){return <article className="job-card"><div className="job-card-top"><div className="company-logo">{job.company[0]}</div><button className={"heart "+(saved?"active":"")} onClick={toggle}><Heart size={17} fill={saved?"currentColor":"none"}/></button></div><div className="job-top"><span className="badge">{job.type}</span><span className="posted">{job.posted}</span></div><h3>{job.title}</h3><p className="company">{job.company}</p><p className="location">⌖ {job.location}</p><div className="tags">{job.tags.map(t=><span key={t}>{t}</span>)}</div><div className="card-footer"><div><b>{job.salary}</b><small>{job.level}</small></div><button className="apply" onClick={()=>notify("Application flow started for "+job.title)}>Apply now</button></div></article>}
function Companies({onView}){return <section id="companies" className="section companies"><div className="section-head"><div><div className="kicker">HIRING TEAMS</div><h2>Companies hiring now</h2></div><button className="text-btn" onClick={onView}>Explore companies <ChevronRight size={15}/></button></div><div className="company-grid">{["Nova Systems","BluePeak Labs","OrbitWorks","Vertex Analytics"].map((c,i)=><div className="company-card" key={c} onClick={onView}><div className="company-logo">{c[0]}</div><div><b>{c}</b><small>{[42,28,19,35][i]} open roles</small></div><ChevronRight size={17}/></div>)}</div></section>}
function Role({icon,title,text}){return <div className="role-card"><div className="role-icon">{icon}</div><h3>{title}</h3><p>{text}</p></div>}
function CTA({onClick}){return <section className="cta"><div><div className="kicker light">START YOUR JOURNEY</div><h2>Your next opportunity starts here.</h2><p>Create a profile and start exploring relevant roles.</p></div><button className="btn white" onClick={onClick}>Create account</button></section>}

function JobsPage({jobs:initialJobs,query,setQuery,location,setLocation,saved,setSaved,notify}){const [jobs,setJobs]=useState(initialJobs); React.useEffect(()=>{api.jobs(query).then(setJobs).catch(()=>{});},[query]); return <main className="inner-page"><div className="page-heading"><div><div className="kicker">JOB DISCOVERY</div><h1>Find your next role</h1><p>Search, filter and save opportunities that match your skills.</p></div></div><SearchBox query={query} setQuery={setQuery} location={location} setLocation={setLocation} onSearch={()=>{}}/><div className="content-layout"><aside className="filters"><b><Filter size={16}/> Filters</b><label>Job type<select><option>All types</option><option>Full-time</option><option>Hybrid</option><option>Remote</option></select></label><label>Experience<select><option>Any experience</option><option>Fresher</option><option>Junior</option><option>Mid-level</option></select></label><label>Work mode<select><option>Any mode</option><option>Remote</option><option>Hybrid</option><option>On-site</option></select></label><button className="clear-filter" onClick={()=>{setQuery("");setLocation("")}}>Clear filters</button></aside><section><div className="results-head"><b>{jobs.length} jobs found</b><select><option>Most relevant</option><option>Newest</option></select></div><div className="job-list">{jobs.map(j=><JobCard key={j.title} job={j} saved={saved.includes(j.title)} toggle={()=>setSaved(saved.includes(j.title)?saved.filter(x=>x!==j.title):[...saved,j.title])} notify={notify}/>)}</div></section></div></main>}
function CompaniesPage({onView}){return <main className="inner-page"><div className="page-heading"><div><div className="kicker">COMPANIES</div><h1>Explore hiring companies</h1><p>Discover teams, open roles, and opportunities across industries.</p></div></div><div className="company-directory">{["Nova Systems","BluePeak Labs","OrbitWorks","Vertex Analytics","CloudNova","Nexora Technologies","Aster Digital","FinEdge"].map((c,i)=><div className="directory-card"><div className="company-logo">{c[0]}</div><h3>{c}</h3><span>{12+i*5} open positions</span><button onClick={()=>onView(c)}>View company</button></div>)}</div></main>}
function Dashboard({role,go,notify}){return <main className="inner-page"><div className="dashboard-head"><div><div className="kicker">MY WORKSPACE</div><h1>{role==="recruiter"?"Recruiter dashboard":"Candidate dashboard"}</h1><p>Manage your recruitment activity from one place.</p></div><div className="dashboard-actions"><button className="btn ghost" onClick={()=>notify("Profile settings opened")}>Profile settings</button><button className="btn primary" onClick={()=>role==="recruiter"?go("post"):go("jobs")}>{role==="recruiter"?"Post a job":"Find jobs"}</button></div></div><div className="dashboard-grid"><div className="dash-card"><span className="dash-icon"><BriefcaseBusiness/></span><b>{role==="recruiter"?"12":"24"}</b><small>{role==="recruiter"?"Active job posts":"Applications sent"}</small></div><div className="dash-card"><span className="dash-icon"><FileText/></span><b>{role==="recruiter"?"86":"8"}</b><small>{role==="recruiter"?"Applications received":"Saved jobs"}</small></div><div className="dash-card"><span className="dash-icon"><Activity/></span><b>{role==="recruiter"?"18":"3"}</b><small>{role==="recruiter"?"Candidates shortlisted":"Interviews scheduled"}</small></div><div className="dash-card"><span className="dash-icon"><Bell/></span><b>5</b><small>New notifications</small></div></div><div className="workspace-card"><div className="section-head"><h2>Recent activity</h2><button className="text-btn" onClick={()=>notify("All activity loaded")}>View all</button></div>{["Profile updated","New application received","Application moved to interview","New recommended job"].map((x,i)=><div className="timeline"><span><Clock3 size={15}/></span><div><b>{x}</b><small>{i+1} hours ago · JobSphere</small></div></div>)}</div></main>}
function Auth({mode,setRole,go,notify}){const [email,setEmail]=useState("");const [password,setPassword]=useState("");const [name,setName]=useState("");return <main className="auth-page"><form className="auth-card" onSubmit={async e=>{e.preventDefault();try{const result=mode==="login"?await api.login({email,password}):await api.register({name,email,password,role});localStorage.setItem("jobsphereUser",JSON.stringify(result));notify(mode==="login"?"Signed in successfully":"Account created successfully");go("dashboard")}catch(err){notify(err.message)}}}><div className="auth-brand"><span className="brand-mark"><BriefcaseBusiness/></span><h2>{mode==="login"?"Welcome back":"Create your JobSphere account"}</h2><p>{mode==="login"?"Sign in to continue to your workspace.":"Join candidates and recruiters building what’s next."}</p></div>{mode==="register"&&<><div className="role-switch"><button type="button" className={role==="candidate"?"selected":""} onClick={()=>setRole("candidate")}><UserRound/> Candidate</button><button type="button" className={role==="recruiter"?"selected":""} onClick={()=>setRole("recruiter")}><Building2/> Recruiter</button></div><label>Full name<input required value={name} onChange={e=>setName(e.target.value)} placeholder="Your name"/></label></>}<label>Email address<input required type="email" value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com"/></label><label>Password<input required type="password" value={password} onChange={e=>setPassword(e.target.value)} placeholder="••••••••"/></label><button className="btn primary full">{mode==="login"?"Sign in":"Create account"}</button><div className="auth-link">{mode==="login"?"New to JobSphere?":"Already have an account?"} <button type="button" onClick={()=>go(mode==="login"?"register":"login")}>{mode==="login"?"Create account":"Sign in"}</button></div></form></main>}

function PostJob({go,notify}){const [form,setForm]=useState({title:"",company:"",location:"",type:"Full-time",level:"Fresher",salary:"",skills:"",description:""});return <main className="inner-page narrow"><div className="page-heading"><div><div className="kicker">RECRUITER TOOLS</div><h1>Post a new job</h1><p>Give candidates the information they need to understand the opportunity.</p></div></div><form className="form-card" onSubmit={async e=>{e.preventDefault();try{const u=JSON.parse(localStorage.getItem("jobsphereUser")||"{}");await api.createJob({...form,recruiterId:u.id});notify("Job posted successfully");go("dashboard")}catch(err){notify(err.message)}}}><div className="form-grid"><label>Job title<input required value={form.title} onChange={e=>setForm({...form,title:e.target.value})} placeholder="e.g. Java Full Stack Developer"/></label><label>Company name<input required value={form.company} onChange={e=>setForm({...form,company:e.target.value})} placeholder="Company name"/></label><label>Location<input required value={form.location} onChange={e=>setForm({...form,location:e.target.value})} placeholder="Hyderabad / Remote"/></label><label>Job type<select value={form.type} onChange={e=>setForm({...form,type:e.target.value})}><option>Full-time</option><option>Part-time</option><option>Contract</option><option>Internship</option></select></label><label>Experience level<select value={form.level} onChange={e=>setForm({...form,level:e.target.value})}><option>Fresher</option><option>Junior</option><option>Mid-level</option><option>Senior</option></select></label><label>Salary range<input value={form.salary} onChange={e=>setForm({...form,salary:e.target.value})} placeholder="₹6–10 LPA"/></label></div><label>Skills required<input value={form.skills} onChange={e=>setForm({...form,skills:e.target.value})} placeholder="Java, Spring Boot, SQL, React"/></label><label>Job description<textarea required rows="7" value={form.description} onChange={e=>setForm({...form,description:e.target.value})} placeholder="Describe responsibilities, requirements and what the team offers..."/></label><div className="form-actions"><button type="button" className="btn ghost" onClick={()=>go("dashboard")}>Cancel</button><button className="btn primary">Publish job</button></div></form></main>}

function HowPage({go}){return <main className="inner-page"><div className="page-heading"><div><div className="kicker">HOW IT WORKS</div><h1>Recruitment, organized.</h1><p>Everything a modern job marketplace needs in one connected workflow.</p></div></div><div className="steps">{[["01","Create your profile","Candidates build professional profiles; recruiters create company pages."],["02","Discover opportunities","Search jobs, filter results, review requirements and save interesting roles."],["03","Apply or shortlist","Candidates submit applications while recruiters review and shortlist talent."],["04","Track progress","Application statuses, interviews, notifications and recruitment activity stay visible."]].map(x=><div className="step"><span>{x[0]}</span><div><h3>{x[1]}</h3><p>{x[2]}</p></div></div>)}</div><CTA onClick={()=>go("register")}/></main>}

createRoot(document.getElementById("root")).render(<React.StrictMode><App/></React.StrictMode>);
