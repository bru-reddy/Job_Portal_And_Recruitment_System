import React,{useEffect,useMemo,useState} from "react";
import {createRoot} from "react-dom/client";
import {ArrowRight,ArrowLeft,BriefcaseBusiness,Building2,CheckCircle2,ChevronRight,Clock3,Globe,GraduationCap,LogIn,MapPin,Menu,Search,ShieldCheck,BookOpen,Code2,Database,BrainCircuit,MonitorCog,FlaskConical,Palette,BarChart3,Users,Plus,ExternalLink,X} from "lucide-react";
import "./styles.css";
import {api} from "./api";

const DATA_RESET_VERSION="3";
if(localStorage.getItem("jobsphereDataVersion")!==DATA_RESET_VERSION){localStorage.removeItem("jobsphereUser");localStorage.setItem("jobsphereDataVersion",DATA_RESET_VERSION);}

const companies=[
["Microsoft","Technology & Cloud","M","https://www.microsoft.com/","https://careers.microsoft.com/"],
["Google","Technology & AI","G","https://www.google.com/","https://careers.google.com/jobs/"],
["Amazon","Technology & Commerce","A","https://www.amazon.com/","https://www.amazon.jobs/"],
["Apple","Consumer Technology","A","https://www.apple.com/","https://www.apple.com/careers/in/"],
["Meta","Social Technology & AI","M","https://www.meta.com/","https://www.metacareers.com/"],
["NVIDIA","AI & Computing","N","https://www.nvidia.com/","https://www.nvidia.com/en-in/about-nvidia/careers/"],
["IBM","Enterprise Technology","I","https://www.ibm.com/","https://www.ibm.com/careers"],
["Accenture","Technology Services","A","https://www.accenture.com/","https://www.accenture.com/in-en/careers"],
["Infosys","IT Services","I","https://www.infosys.com/","https://www.infosys.com/careers.html"],
["TCS","IT Services","T","https://www.tcs.com/","https://www.tcs.com/careers"],
["Oracle","Cloud & Database Technology","O","https://www.oracle.com/","https://www.oracle.com/in/careers/"],
["Adobe","Digital Technology","A","https://www.adobe.com/","https://www.adobe.com/careers.html"]
].map(([name,industry,initial,site,careers])=>({name,industry,initial,site,careers}));

const tracks={
Technology:[
["Java",Code2,"https://dev.java/learn/","https://www.hackerrank.com/domains/java","Core Java, OOP, collections, streams and modern Java."],
["Python",Code2,"https://docs.python.org/3/tutorial/","https://www.hackerrank.com/domains/python","Python language fundamentals and programming practice."],
["C++",Code2,"https://www.learncpp.com/","https://www.hackerrank.com/domains/cpp","Modern C++ from fundamentals through advanced topics."],
["Data Structures & Algorithms",BarChart3,"https://visualgo.net/en","https://leetcode.com/problemset/","Data structures, algorithms, visualization and coding problems."],
["Operating Systems",MonitorCog,"https://ocw.mit.edu/courses/6-1810-operating-system-engineering-fall-2023/","https://www.geeksforgeeks.org/operating-systems/","Processes, memory, file systems, threads, kernels and systems."],
["Computer Networks",Globe,"https://www.geeksforgeeks.org/computer-network-tutorials/","https://www.geeksforgeeks.org/computer-networks-gq/","Protocols, networking fundamentals and interview practice."],
["DBMS & SQL",Database,"https://www.postgresql.org/docs/current/tutorial.html","https://www.hackerrank.com/domains/sql","SQL, relational databases, normalization and transactions."],
["Compiler Design",Code2,"https://www.geeksforgeeks.org/compiler-design-tutorials/","https://www.geeksforgeeks.org/compiler-design/compiler-design-for-gate/","Lexical analysis, parsing, semantic analysis and optimization."],
["Big Data",Database,"https://spark.apache.org/docs/latest/quick-start.html","https://spark.apache.org/docs/latest/quick-start.html","Distributed processing and Apache Spark foundations."],
["Machine Learning & AI",BrainCircuit,"https://developers.google.com/machine-learning/crash-course","https://developers.google.com/machine-learning/crash-course/exercises","Practical ML concepts, interactive exercises and production basics."]
],
Science:[
["Physics",FlaskConical,"https://openstax.org/subjects/science","https://openstax.org/subjects/science","Open educational science resources."],
["Chemistry",FlaskConical,"https://openstax.org/subjects/science","https://openstax.org/subjects/science","Open educational chemistry resources."],
["Mathematics",BarChart3,"https://www.khanacademy.org/math","https://www.khanacademy.org/math","Structured mathematics learning and practice."]
],
Business:[
["Finance",BarChart3,"https://www.khanacademy.org/economics-finance-domain","https://www.khanacademy.org/economics-finance-domain","Finance and economics fundamentals."],
["Marketing",Users,"https://academy.hubspot.com/","https://academy.hubspot.com/","Marketing, CRM and business learning."],
["Project Management",GraduationCap,"https://www.pmi.org/learning","https://www.pmi.org/learning","Project planning and delivery resources."]
],
Design:[
["UI/UX Design",Palette,"https://www.coursera.org/browse/design/user-experience","https://www.figma.com/resources/learn-design/","User experience and interface design resources."]
]
};

function companyByName(n){return companies.find(c=>c.name.toLowerCase()===String(n||"").toLowerCase())}
function getStoredUser(){
 try{
  const u=JSON.parse(localStorage.getItem("jobsphereUser")||"null");
  return u&&(u.role==="CANDIDATE"||u.role==="RECRUITER")?u:null;
 }catch{return null}
}
function App(){
 const [screen,setScreen]=useState(()=>getStoredUser()?.role==="RECRUITER"?"dashboard":"home"),[menu,setMenu]=useState(false),[query,setQuery]=useState(""),[jobs,setJobs]=useState([]),[user,setUser]=useState(()=>{try{const u=JSON.parse(localStorage.getItem("jobsphereUser")||"null");return u&&(u.role==="CANDIDATE"||u.role==="RECRUITER")?u:null}catch{return null}}),[toast,setToast]=useState(null);
 const [selectedJob,setSelectedJob]=useState(null),[selectedCompany,setSelectedCompany]=useState(null),[authMode,setAuthMode]=useState("login"),[authRole,setAuthRole]=useState("CANDIDATE"),[skillMode,setSkillMode]=useState(null),[skillCategory,setSkillCategory]=useState(null);
 const loadJobs=async()=>{
  try{
   const x=await api.jobs();
   const list=Array.isArray(x)?x:(Array.isArray(x?.jobs)?x.jobs:[]);
   setJobs(list.map(j=>({...j,skills:Array.isArray(j.skills)?j.skills.join(", "):(j.skills||"")})));
  }catch{
   setJobs([]);
  }
 };
 useEffect(()=>{loadJobs()},[user?.id,screen]);
 useEffect(()=>{
  if(!user)return;
  const recruiterScreens=["dashboard","recruiterJobs","recruiterApplicants","post"];
  const candidateScreens=["home","jobs","companies","companyProfile","apply","dashboard","skills","auth"];
  if(user.role==="RECRUITER"&&!recruiterScreens.includes(screen))setScreen("dashboard");
  if(user.role==="CANDIDATE"&&!candidateScreens.includes(screen))setScreen("dashboard");
 },[user,screen]);
 const go=s=>{setScreen(s);setMenu(false);window.scrollTo(0,0)},notify=(m,t="success")=>{setToast({m,t});setTimeout(()=>setToast(null),3200)};
 useEffect(()=>{
  const handler=e=>notify(e.detail?.message||"Updated",e.detail?.type||"success");
  window.addEventListener("jobsphere-toast",handler);
  return()=>window.removeEventListener("jobsphere-toast",handler);
 },[]);
 const auth=(m,r)=>{setAuthMode(m);setAuthRole(r);go("auth")};
 const logout=()=>{localStorage.removeItem("jobsphereUser");setUser(null);notify("Signed out");go("home")};
 const apply=job=>{if(!user){auth("login","CANDIDATE");return}if(user.role!=="CANDIDATE"){notify("Only candidate accounts can apply","error");return}setSelectedJob(job);go("apply")};
 const openCompany=n=>{
  const name=String(n||"").trim();
  const known=companyByName(name);
  if(known){setSelectedCompany(known);go("companyProfile");return;}
  const posted=jobs.find(j=>String(j.company||"").trim().toLowerCase()===name.toLowerCase());
  if(posted){
   setSelectedCompany({
    name,
    industry:"Recruiter-posted opportunities",
    initial:name[0]?.toUpperCase()||"C",
    site:posted.sourceUrl||"",
    careers:posted.sourceUrl||""
   });
   go("companyProfile");
  }
 };
 const navigation=()=>!user?[["Find Jobs","jobs"],["Companies","companies"],["Build Your Skills","skills"]]:user.role==="CANDIDATE"?[["Find Jobs","jobs"],["Companies","companies"],["Build Your Skills","skills"],["Dashboard","dashboard"]]:[["Manage Jobs","recruiterJobs"],["Applicants","recruiterApplicants"],["Dashboard","dashboard"]];
 return <div className="app-shell">
  <header className="navbar"><button className="brand brand-button" onClick={()=>go("home")}><span className="brand-mark"><BriefcaseBusiness size={18}/></span>JobSphere</button><nav>{navigation().map(([x,s])=><button key={x} className={screen===s?"active":""} onClick={()=>go(s)}>{x}</button>)}</nav><div className="nav-actions">{user?<><button className="user-chip" onClick={()=>go("dashboard")}><span>{(user.name||"U")[0].toUpperCase()}</span>{user.role==="CANDIDATE"?user.name:(user.companyName||user.name)}</button><button className="btn ghost" onClick={logout}>Sign out</button></>:<><button className="btn ghost" onClick={()=>auth("login","CANDIDATE")}><LogIn size={15}/> Sign in</button><button className="btn primary" onClick={()=>auth("register","CANDIDATE")}>Create account <ArrowRight size={14}/></button></>}</div><button className="mobile-menu" onClick={()=>setMenu(!menu)}>{menu?<X/>:<Menu/>}</button></header>
  {menu&&<div className="mobile-nav">
  {navigation().map(([x,s])=><button key={x} onClick={()=>go(s)}>{x}</button>)}
  <div className="mobile-nav-actions">
   {user?<button className="mobile-nav-signout" onClick={logout}>Sign out</button>:<>
    <button className="mobile-nav-signin" onClick={()=>auth("login","CANDIDATE")}>Sign in</button>
    <button className="mobile-nav-create" onClick={()=>auth("register","CANDIDATE")}>Create account <ArrowRight size={14}/></button>
   </>}
  </div>
 </div>}
  {screen==="home"&&<Home go={go} jobs={jobs} apply={apply} openCompany={openCompany}/>}
  {screen==="jobs"&&<Jobs jobs={jobs} query={query} setQuery={setQuery} apply={apply} openCompany={openCompany}/>}
  {screen==="companies"&&<Companies openCompany={openCompany} jobs={jobs}/>}
  {screen==="companyProfile"&&<CompanyProfile company={selectedCompany} jobs={jobs} apply={apply} go={go}/>}
  {screen==="apply"&&<ApplicationForm job={selectedJob} user={user} go={go} notify={notify}/>}
  {screen==="dashboard"&&<Dashboard user={user} go={go} jobs={jobs} apply={apply}/>}
  {screen==="recruiter"&&<RecruiterDashboard user={user} go={go}/>}
  {screen==="recruiterApplicants"&&<RecruiterApplicants user={user}/>}
  {screen==="recruiterJobs"&&<RecruiterJobs user={user} go={go}/>}
    {screen==="post"&&<PostJob user={user} go={go} notify={notify}/>}
  {screen==="auth"&&<Auth mode={authMode} role={authRole} setMode={setAuthMode} setRole={setAuthRole} setUser={setUser} go={go} notify={notify}/>}
  {screen==="skills"&&<Skills mode={skillMode} setMode={setSkillMode} category={skillCategory} setCategory={setSkillCategory}/>}
  <footer><span className="brand"><span className="brand-mark"><BriefcaseBusiness size={15}/></span>JobSphere</span><span>Java Full Stack Job Portal & Recruitment System</span><span>Candidate · Recruiter workspaces</span></footer>
  {toast&&<div className={"toast "+toast.t}>{toast.t==="success"?<CheckCircle2 size={17}/>:<X size={17}/>} {toast.m}</div>}
 </div>
}

function Home({go,jobs,apply,openCompany}){return <main><section className="hero"><div><div className="eyebrow">✦ JAVA FULL STACK RECRUITMENT WORKSPACE</div><h1>Find roles. Build skills. Move forward.</h1><p className="hero-lead">A professional recruitment workspace with separate candidate and Recruitment Partner responsibilities.</p><div className="search-panel"><div className="search-field"><Search size={18}/><input placeholder="Job title, skill or company" onKeyDown={e=>e.key==="Enter"&&go("jobs")}/></div><button className="btn primary" onClick={()=>go("jobs")}>Search jobs</button></div><div className="hero-trust"><span><CheckCircle2 size={14}/> Candidate applications</span><span><ShieldCheck size={14}/> Role-based access</span><span><GraduationCap size={14}/> Skill development</span></div></div><div className="hero-panel"><div className="kicker">WORKSPACE MODEL</div><h3>Two workspaces. Clear boundaries.</h3>{[["Candidate","Discover jobs, apply and track your submissions."],["Recruitment Partner","Source jobs externally, post them and screen applicants." ]].map(([a,b])=><div className="overview-row" key={a}><b>{a}</b><small>{b}</small></div>)}</div></section><section className="stats"><div><b>{jobs.length}</b><span>Open opportunities</span></div><div><b>2</b><span>Workspaces</span></div><div><b>0</b><span>Preloaded jobs</span></div><div><b>2</b><span>Skill modes</span></div></section><section className="section"><div className="section-head"><div><div className="kicker">OPEN OPPORTUNITIES</div><h2>Find your next role</h2><p>Listings are demo entries sourced from external job channels; the source is shown on each listing.</p></div><button className="text-btn" onClick={()=>go("jobs")}>Browse jobs <ArrowRight size={15}/></button></div><div className="job-grid">{jobs.slice(0,6).map(j=><JobCard key={j.id} job={j} apply={()=>apply(j)} openCompany={openCompany}/>)}</div></section><section className="section split-section"><div><div className="kicker">BUILD YOUR SKILLS</div><h2>Learn deliberately. Practice deliberately.</h2><p className="section-sub big">Choose Learn or Practice, select a field, then open a curated external resource for that topic.</p><button className="btn primary" onClick={()=>go("skills")}>Build Your Skills <GraduationCap size={15}/></button></div><div className="skill-preview"><div><BookOpen/><b>Learn</b><small>Professional courses and tutorials</small></div><div><Code2/><b>Practice</b><small>Hands-on coding and exercises</small></div></div></section></main>}

function JobCard({job,apply,openCompany}){return <article className="job-card"><div className="job-card-top"><button className="company-logo-button" onClick={()=>openCompany(job.company)}><CompanyLogo name={job.company}/></button><span className="badge">{job.type||"Full-time"}</span></div><span className="posted">{job.level}</span><h3>{job.title}</h3><button className="company-link" onClick={()=>openCompany(job.company)}>{job.company}</button><p className="location"><MapPin size={13}/> {job.location}</p><div className="tags">{String(job.skills||"").split(",").filter(Boolean).slice(0,4).map(x=><span key={x}>{x.trim()}</span>)}</div><div className="eligibility"><b>Eligibility</b><span>{job.eligibilityCriteria||"Role-specific criteria"}</span></div>{job.sourceUrl&&<a className="job-source" href={job.sourceUrl} target="_blank" rel="noreferrer"><ExternalLink size={12}/> Source: {job.sourceName||"External listing"}</a>}<div className="card-footer"><div><b>{job.salary||"Salary not disclosed"}</b><small>{job.level}</small></div><button className="apply" onClick={apply}>Apply <ArrowRight size={13}/></button></div></article>}

function Jobs({jobs,query,setQuery,apply,openCompany}){const [loading,setLoading]=useState(!jobs.length),[localJobs,setLocalJobs]=useState(jobs);
 useEffect(()=>{let live=true;setLoading(true);api.jobs().then(x=>{const list=Array.isArray(x)?x:(Array.isArray(x?.jobs)?x.jobs:[]);if(live)setLocalJobs(list.map(j=>({...j,skills:Array.isArray(j.skills)?j.skills.join(", "):(j.skills||"")})));}).catch(()=>{}).finally(()=>{if(live)setLoading(false)});return()=>{live=false}},[]);
 useEffect(()=>{if(jobs.length)setLocalJobs(jobs)},[jobs]);
 const list=useMemo(()=>localJobs.filter(j=>!query||[j.title,j.company,j.skills,j.location].join(" ").toLowerCase().includes(query.toLowerCase())),[localJobs,query]);
 return <main className="inner-page"><div className="page-heading"><div className="kicker">JOB DISCOVERY</div><h1>Find Jobs</h1><p>Search by role, company, location or skill.</p></div><div className="search-panel compact-search"><div className="search-field"><Search size={18}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search jobs, companies or skills"/></div></div>{loading&&!list.length?<div className="empty-inline">Loading active recruiter-posted jobs...</div>:list.length?<div className="job-grid">{list.map(j=><JobCard key={j.id} job={j} apply={()=>apply(j)} openCompany={openCompany}/>)}</div>:<div className="empty-state"><BriefcaseBusiness/><h2>No active jobs yet</h2><p>Recruiter-posted jobs will appear here automatically.</p></div>}</main>}

function Companies({openCompany,jobs=[]}){const posted=[...new Set(jobs.map(j=>String(j.company||"").trim()).filter(Boolean))].map(name=>({name,industry:"Recruiter-posted opportunities",initial:name[0].toUpperCase()}));return <main className="inner-page"><div className="page-heading"><div className="kicker">COMPANY DIRECTORY</div><h1>Companies</h1><p>Companies shown here are taken directly from active jobs posted on JobSphere.</p></div>{posted.length?<div className="company-directory">{posted.map(c=><button className="directory-card" key={c.name} onClick={()=>openCompany(c.name)}><div className="directory-top"><CompanyLogo name={c.name}/><span>{c.industry}</span></div><h3>{c.name}</h3><p>View the active roles posted for this company on JobSphere.</p><span className="directory-link">View jobs <ArrowRight size={14}/></span></button>)}</div>:<div className="empty-state"><Building2/><h2>No companies yet</h2><p>Companies will appear here after a recruiter publishes a job.</p></div>}</main>}

function CompanyProfile({company,jobs,apply,go}){if(!company)return null;const list=jobs.filter(j=>String(j.company||"").toLowerCase()===company.name.toLowerCase());const source=company.site||list.find(j=>j.sourceUrl)?.sourceUrl;return <main className="inner-page"><button className="back-btn" onClick={()=>go("companies")}><ArrowLeft size={15}/> Back</button><section className="company-hero-card"><div className="company-hero-logo"><CompanyLogo name={company.name}/></div><div><div className="kicker">RECRUITER-POSTED COMPANY</div><h1>{company.name}</h1><p>{company.industry||"Company represented by recruiter-posted jobs"}</p>{source&&<div className="company-actions"><a className="btn ghost" href={source} target="_blank" rel="noreferrer"><ExternalLink size={14}/> Job source</a></div>}</div></section><section className="workspace-card"><div className="kicker">ACTIVE OPENINGS</div><h2>Roles posted on JobSphere</h2><p className="muted">These listings come directly from active recruiter postings.</p>{list.length?<div className="job-grid">{list.map(j=><JobCard key={j.id} job={j} apply={()=>apply(j)} openCompany={()=>{}}/>)}</div>:<div className="empty-inline">No active jobs for this company.</div>}</section></main>}

function ApplicationForm({job,user,go,notify}){const [f,setF]=useState({name:user?.name||"Padma Rao",email:user?.email||"padma.demo@gmail.com",phone:"9876543210",education:"B.Tech in Computer Science & Engineering",experience:"Fresher",skills:"Java, Spring Boot, SQL, React, DSA",coverLetter:"I am a Computer Science graduate with a strong foundation in Java, data structures, SQL and full-stack development. I am interested in this opportunity and would be excited to contribute while continuing to grow professionally."}),[busy,setBusy]=useState(false),[error,setError]=useState("");const change=e=>setF({...f,[e.target.name]:e.target.value});const submit=async e=>{e.preventDefault();setError("");setBusy(true);try{await api.apply({jobId:job.id,candidateId:user?.id,candidateEmail:user?.email,applicantName:f.name,applicantEmail:f.email,applicantPhone:f.phone,education:f.education,experience:f.experience,skills:f.skills,coverLetter:f.coverLetter});notify("Application submitted successfully");go("dashboard")}catch(x){setError(x.message)}finally{setBusy(false)}};return <main className="auth-page"><div className="application-layout"><div className="application-summary"><button className="back-btn" onClick={()=>go("jobs")}><ArrowLeft size={15}/> Back to jobs</button><div className="kicker">APPLICATION</div><h1>{job.title}</h1><button className="company-link large">{job.company}</button><p><MapPin size={14}/> {job.location}</p><div className="application-notice"><ShieldCheck size={16}/><div><b>External hiring boundary</b><small>The Recruitment Partner reviews your application for skill relevance. If approved, your profile is forwarded to the company externally. Resume handling, interviews and hiring decisions are handled by the company.</small></div></div><div className="summary-list"><span><b>Eligibility</b>{job.eligibilityCriteria}</span><span><b>Skills</b>{job.skills}</span><span><b>Compensation</b>{job.salary||"Not disclosed"}</span></div></div><form className="form-card" onSubmit={submit}><div className="form-heading"><div><div className="kicker">CANDIDATE DETAILS</div><h2>Complete your application</h2><p>Enter your details and relevant skills for screening.</p></div><span className="required-note">* Required</span></div><div className="form-grid"><Field label="Full name *" name="name" value={f.name} onChange={change} required/><Field label="Email *" name="email" type="email" value={f.email} onChange={change} required/><Field label="Phone *" name="phone" value={f.phone} onChange={change} required/><Field label="Education *" name="education" value={f.education} onChange={change} placeholder="B.Tech CSE, MCA..." required/><Field label="Experience" name="experience" value={f.experience} onChange={change} placeholder="Fresher / 1 year"/><Field label="Skills *" name="skills" value={f.skills} onChange={change} placeholder="Java, SQL, DSA" required/></div><label className="field full-field">Additional information<textarea name="coverLetter" value={f.coverLetter} onChange={change} rows="5" placeholder="Briefly describe your fit for the role..."/></label>{error&&<div className="error-box"><X size={15}/>{error}</div>}<button className="btn primary full submit-btn" disabled={busy}>{busy?"Submitting...":"Submit application"} <ArrowRight size={15}/></button></form></div></main>}

function Dashboard({user,go,jobs,apply}){if(!user)return <main className="inner-page"><div className="empty-state"><BriefcaseBusiness/><h2>Sign in to access your dashboard</h2><button className="btn primary" onClick={()=>go("auth")}>Sign in</button></div></main>;if(user.role==="RECRUITER")return <RecruiterDashboard user={user} go={go}/>;return <CandidateDashboard user={user} go={go} jobs={jobs} apply={apply}/>}

function CandidateDashboard({user,go,jobs,apply}){const [apps,setApps]=useState([]),[loading,setLoading]=useState(true);
 const loadApplications=()=>{setLoading(true);api.applications(user.id).then(x=>setApps(Array.isArray(x)?x:[])).catch(()=>setApps([])).finally(()=>setLoading(false))};
 useEffect(loadApplications,[user.id]);
return <main className="inner-page"><div className="dashboard-head candidate-head"><div><div className="kicker">CANDIDATE DASHBOARD</div><h1>Welcome, {user.name.split(" ")[0]}.</h1><p>Discover opportunities, track applications and build your skills.</p></div><button className="btn primary" onClick={()=>go("jobs")}>Find Jobs <ArrowRight size={14}/></button></div><div className="dashboard-grid"><Dash value={apps.length} label="Applications sent" icon={<BriefcaseBusiness/>}/><Dash value={apps.filter(a=>a.status==="APPLIED").length} label="Submitted" icon={<Clock3/>}/><Dash value="External" label="External company process" icon={<ShieldCheck/>}/><Dash value="∞" label="Learning paths" icon={<GraduationCap/>}/></div><section className="workspace-card"><div className="kicker">APPLICATION TRACKER</div><h2>Your applications</h2>{loading?<div className="empty-inline">Loading your applications...</div>:apps.length?apps.map(a=><div className="application-row" key={a.id}><div className="application-main"><span className="status-avatar pending"><Clock3 size={17}/></span><div><b>{a.job?.title}</b><small>{a.job?.company} · {a.appliedAt?new Date(a.appliedAt).toLocaleDateString():"Recently"}</small><p className="decision-message">{a.status==="APPROVED_FOR_REFERRAL"?"Your profile has been approved for referral. The Recruitment Partner will send it to the company externally.":a.status==="NOT_SELECTED"?"This application was not selected for referral.":"Your application is being reviewed by the Recruitment Partner."}</p></div></div><span className={"status "+(a.status==="APPROVED_FOR_REFERRAL"?"status-accepted":a.status==="NOT_SELECTED"?"status-rejected":"status-applied")}>{a.status==="APPROVED_FOR_REFERRAL"?"APPROVED FOR REFERRAL":a.status==="NOT_SELECTED"?"NOT SELECTED":a.status||"APPLIED"}</span></div>):<div className="empty-inline">No applications yet. Find a job and submit your candidate information.</div>}</section><section className="section compact"><div className="section-head"><div><div className="kicker">RECOMMENDED</div><h2>Explore more roles</h2></div></div><div className="job-grid">{jobs.slice(0,4).map(j=><JobCard key={j.id} job={j} apply={()=>apply(j)} openCompany={()=>{}}/>)}</div></section></main>}

function RecruiterDashboard({user,go}){const [apps,setApps]=useState([]),[jobs,setJobs]=useState([]);
useEffect(()=>{Promise.all([api.recruiterApplications(user.id),api.recruiterJobs(user.id)]).then(([a,j])=>{setApps(a);setJobs(Array.isArray(j)?j:[])}).catch(()=>{setApps([]);setJobs([])})},[user.id]);
const countForJob=id=>apps.filter(a=>a.job?.id===id).length;
return <main className="inner-page recruiter-dashboard">
<div className="dashboard-head recruiter-head"><div><div className="kicker">RECRUITMENT PARTNER DASHBOARD</div><h1>Welcome, {user.name.split(" ")[0]}.</h1><p>Source opportunities, publish roles and review candidate applications.</p></div><button className="btn primary" onClick={()=>go("post")}><Plus size={15}/> Post a job</button></div>
<div className="dashboard-grid recruiter-summary">
<Dash value={jobs.length} label="Posted jobs" icon={<BriefcaseBusiness/>}/>
<Dash value={apps.length} label="Total applicants" icon={<Users/>}/>

</div>
<section className="workspace-card recruiter-overview"><div className="section-head"><div><div className="kicker">POSTED JOBS</div><h2>Your job postings</h2><p className="muted">Every job you publish is stored in the database and remains available after refresh or sign-in.</p></div><button className="btn ghost" onClick={()=>go("recruiterJobs")}>Manage jobs <ArrowRight size={14}/></button></div>
{jobs.length?<div className="job-admin-list">{jobs.map(j=><div className="job-admin-row" key={j.id}><div><b>{j.title}</b><small>{j.location} · {j.type} · {j.level}</small><span><strong>{countForJob(j.id)}</strong> applicant{countForJob(j.id)===1?"":"s"} · <strong>Eligibility:</strong> {j.eligibilityCriteria||"Role-specific requirements"}</span></div><div className="tags">{String(j.skills||"").split(",").filter(Boolean).map(x=><span key={x}>{x.trim()}</span>)}</div></div>)}</div>:<div className="empty-inline">No jobs posted yet. Use “Post a job” to create your first listing.</div>}</section>
<section className="workspace-card recruiter-tools"><div className="kicker">RECRUITMENT PARTNER TOOLS</div><h2>Next actions</h2><p className="muted">Quick access to the actions you use most.</p><div className="quick-action-grid">
<button className="quick-action" onClick={()=>go("post")}><span className="quick-action-icon"><Plus/></span><span className="quick-action-copy"><b>Create a job</b><small>Publish a new role with requirements, skills and eligibility.</small></span><span className="quick-action-arrow"><ArrowRight/></span></button>
<button className="quick-action" onClick={()=>go("recruiterApplicants")}><span className="quick-action-icon"><Users/></span><span className="quick-action-copy"><b>Review applicants</b><small>Review candidate submissions for your posted roles.</small></span><span className="quick-action-arrow"><ArrowRight/></span></button>
</div></section>
<div className="external-note"><ShieldCheck size={17}/><div><b>Hiring boundary</b><span>JobSphere stores the application and screening decision. Any referral and company hiring process happens outside this portal.</span></div></div>
</main>}

function RecruiterJobs({user,go}){const [jobs,setJobs]=useState([]);useEffect(()=>{api.recruiterJobs(user.id).then(j=>setJobs(Array.isArray(j)?j:[])).catch(()=>setJobs([]))},[user.id]);return <main className="inner-page recruiter-dashboard"><div className="page-heading"><div className="kicker">RECRUITMENT PARTNER · JOB MANAGEMENT</div><h1>Manage Jobs</h1><p>Create, review and monitor the opportunities you source and publish.</p></div><div className="workspace-card"><div className="section-head"><div><div className="kicker">YOUR POSTINGS</div><h2>Active job listings</h2></div><button className="btn primary" onClick={()=>go("post")}><Plus size={15}/> Post a job</button></div>{jobs.length?jobs.map(j=><div className="job-admin-row" key={j.id}><div><b>{j.title}</b><small>{j.location} · {j.type} · {j.level} · {j.salary||"Salary not disclosed"}</small><span><strong>Eligibility:</strong> {j.eligibilityCriteria||"Role-specific requirements"}</span></div><div className="tags">{String(j.skills||"").split(",").filter(Boolean).map(x=><span key={x}>{x.trim()}</span>)}</div></div>):<div className="empty-inline">No jobs posted yet. Use “Post a job” to create your first listing.</div>}</div></main>}

function RecruiterApplicants({user}){const [apps,setApps]=useState([]),[loading,setLoading]=useState(true);
const load=()=>{setLoading(true);api.recruiterApplications(user.id).then(x=>setApps(Array.isArray(x)?x:[])).catch(()=>setApps([])).finally(()=>setLoading(false))};
useEffect(load,[user.id]);
return <main className="inner-page recruiter-applicants"><div className="page-heading"><div className="kicker">RECRUITMENT PARTNER · APPLICANT MANAGEMENT</div><h1>Applicants</h1><p>Review candidate submissions received for your posted roles.</p></div>
<div className="workspace-card"><div className="section-head"><div><div className="kicker">CANDIDATE SUBMISSIONS</div><h2>Applications</h2></div><span className="company-code-badge">{apps.length} TOTAL</span></div>
{loading?<div className="empty-inline">Loading applicants...</div>:apps.length?apps.map(a=><Applicant key={a.id} a={a}/>):<div className="empty-inline">No applicants yet.</div>}</div></main>}

function Applicant({a}){const jobSkills=String(a.job?.skills||"").split(",").map(x=>x.trim().toLowerCase()).filter(Boolean);const candidateSkills=String(a.skills||"").split(",").map(x=>x.trim().toLowerCase()).filter(Boolean);const matched=jobSkills.filter(s=>candidateSkills.some(c=>c===s||c.includes(s)||s.includes(c)));const matchPct=jobSkills.length?Math.round(matched.length/jobSkills.length*100):0;return <div className="applicant-row"><div className="avatar">{(a.applicantName||"?")[0]}</div><div className="applicant-info"><b>{a.applicantName}</b><small>{a.job?.title} · {a.applicantEmail}</small><span>{a.education} · {a.experience||"Experience not specified"}</span><span><strong>Skills:</strong> {a.skills}</span><span className="skill-match"><strong>Skill relevance:</strong> {matchPct}% · {matched.length}/{jobSkills.length} required skills matched</span></div></div>}

function PostJob({user,go,notify}){const [f,setF]=useState({title:"",company:user?.companyName||"",location:"",type:"Full-time",level:"Entry-level",salary:"",skills:"",eligibilityCriteria:"",description:"",sourceName:"",sourceUrl:""}),[busy,setBusy]=useState(false);const ch=e=>setF({...f,[e.target.name]:e.target.value});const submit=async e=>{e.preventDefault();setBusy(true);try{await api.createJob({...f,recruiterId:user.id});notify("Job published");go("dashboard")}catch(x){notify(x.message,"error")}finally{setBusy(false)}};return <main className="auth-page"><form className="form-card post-card" onSubmit={submit}><div className="form-heading"><div><div className="kicker">RECRUITMENT PARTNER WORKSPACE</div><h2>Post a new job</h2><p>Define the role, skills and eligibility criteria.</p></div></div><div className="form-grid"><Field label="Job title *" name="title" value={f.title} onChange={ch} required/><Field label="Company *" name="company" value={f.company} onChange={ch} required/><Field label="Location *" name="location" value={f.location} onChange={ch} required/><Field label="Job type" name="type" value={f.type} onChange={ch}/><Field label="Experience level" name="level" value={f.level} onChange={ch}/><Field label="Salary range" name="salary" value={f.salary} onChange={ch}/><Field label="Required skills *" name="skills" value={f.skills} onChange={ch} placeholder="Java, Spring Boot, SQL" required/><Field label="Eligibility criteria *" name="eligibilityCriteria" value={f.eligibilityCriteria} onChange={ch} placeholder="B.Tech CSE, 0–2 years..." required/><Field label="Job source *" name="sourceName" value={f.sourceName} onChange={ch} placeholder="LinkedIn, company careers..." required/><Field label="Source URL" name="sourceUrl" value={f.sourceUrl} onChange={ch} placeholder="https://..." type="url"/></div><label className="field full-field">Job description<textarea name="description" value={f.description} onChange={ch} rows="6"/></label><button className="btn primary full submit-btn" disabled={busy}>{busy?"Publishing...":"Publish job"} <ArrowRight size={15}/></button></form></main>}

function Auth({mode,role,setMode,setRole,setUser,go,notify}){const register=mode==="register";const [name,setName]=useState(""),[email,setEmail]=useState(""),[password,setPassword]=useState(""),[busy,setBusy]=useState(false),[error,setError]=useState("");const submit=async e=>{e.preventDefault();setError("");setBusy(true);try{const body={name,email,password,role};const x=register?await api.register(body):await api.login(body);const u={...x,role:String(x.role).toUpperCase()};localStorage.setItem("jobsphereUser",JSON.stringify(u));setUser(u);notify(register?"Account created":"Signed in");go("dashboard")}catch(x){setError(x.message)}finally{setBusy(false)}};return <main className="auth-page"><div className="auth-layout"><div className="auth-side"><div className="kicker light">JOBSPHERE</div><h1>{register?"Create the right workspace.":"Welcome back."}</h1><p>Choose Candidate or Recruitment Partner. Each role receives a different workspace and dashboard.</p><div className="auth-proof"><span><CheckCircle2/> Candidate applications</span><span><CheckCircle2/> Recruitment Partner job sourcing and screening</span></div></div><div className="auth-card"><div className="auth-brand"><span className="brand-mark"><BriefcaseBusiness size={17}/></span><h2>{register?"Create account":"Sign in"}</h2><p>Choose the workspace that matches your role.</p></div><div className="role-switch"><button type="button" className={role==="CANDIDATE"?"selected":""} onClick={()=>setRole("CANDIDATE")}><Users size={15}/> Candidate</button><button type="button" className={role==="RECRUITER"?"selected":""} onClick={()=>setRole("RECRUITER")}><BriefcaseBusiness size={15}/> Recruitment Partner</button></div><form onSubmit={submit}>{register&&<Field label="Full name *" value={name} onChange={e=>setName(e.target.value)} required/>}<Field label="Email *" type="email" value={email} onChange={e=>setEmail(e.target.value)} required/><Field label="Password *" type="password" value={password} onChange={e=>setPassword(e.target.value)} minLength={6} required/>{error&&<div className="error-box"><X size={15}/>{error}</div>}<button className="btn primary full submit-btn" disabled={busy}>{busy?(register?"Creating...":"Signing in..."):(register?"Create account":"Sign in")} <ArrowRight size={15}/></button></form><p className="auth-link">{register?"Already have an account?":"Need an account?"} <button onClick={()=>setMode(register?"login":"register")}>{register?"Sign in":"Create account"}</button></p></div></div></main>}

function Skills({mode,setMode,category,setCategory}){const cats=[["Technology",Code2,"Programming, CS fundamentals, databases, AI and systems."],["Science",FlaskConical,"Physics, chemistry and mathematics."],["Business",BarChart3,"Finance, marketing and project management."],["Design",Palette,"UI/UX and digital design."]];if(!mode)return <main className="inner-page"><div className="page-heading"><div className="kicker">SKILL DEVELOPMENT</div><h1>Build Your Skills</h1><p>Choose a mode, then choose a field and topic.</p></div><div className="mode-grid"><button onClick={()=>setMode("learn")}><BookOpen/><h2>Learn</h2><p>Professional tutorials, documentation and courses.</p><ArrowRight/></button><button onClick={()=>setMode("practice")}><Code2/><h2>Practice</h2><p>Problems, exercises, quizzes and hands-on platforms.</p><ArrowRight/></button></div></main>;if(!category)return <main className="inner-page"><button className="back-btn" onClick={()=>setMode(null)}><ArrowLeft size={15}/> Back</button><div className="page-heading"><div className="kicker">{mode.toUpperCase()}</div><h1>Choose a field</h1><p>What would you like to {mode}?</p></div><div className="skill-category-grid">{cats.map(([n,I,d])=><button key={n} onClick={()=>setCategory(n)}><span><I/></span><div><h3>{n}</h3><p>{d}</p></div><ChevronRight/></button>)}</div></main>;const items=tracks[category]||[];return <main className="inner-page"><button className="back-btn" onClick={()=>setCategory(null)}><ArrowLeft size={15}/> Back to fields</button><div className="page-heading"><div className="kicker">{mode.toUpperCase()} · {category.toUpperCase()}</div><h1>{category} {mode==="learn"?"Learning":"Practice"}</h1><p>Select a topic to open the curated external resource.</p></div><div className="resource-grid">{items.map(([n,I,learn,practice,d])=><article className="resource-card" key={n}><span className="resource-icon"><I/></span><h3>{n}</h3><p>{d}</p><a className="resource-btn" href={mode==="learn"?learn:practice} target="_blank" rel="noreferrer">{mode==="learn"?"Open learning resource":"Open practice platform"} <ExternalLink size={13}/></a></article>)}</div></main>}

function CompanyLogo({name}){const c=companyByName(name);return <span className="company-logo">{c?.initial||String(name||"C")[0].toUpperCase()}</span>}
function Dash({icon,value,label}){return <div className="dash-card"><span className="dash-icon">{icon}</span><b>{value}</b><small>{label}</small></div>}
function Field({label,name,type="text",value,onChange,placeholder="",required=false,...p}){return <label className="field"><span>{label}</span><input name={name} type={type} value={value||""} onChange={onChange} placeholder={placeholder} required={required} {...p}/></label>}

createRoot(document.getElementById("root")).render(<App/>);
