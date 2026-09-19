const BASE=(import.meta.env.VITE_API_URL||"").replace(/\/$/,"");

async function request(path,options={}){
  const controller=new AbortController();
  const timer=setTimeout(()=>controller.abort(),90000);
  try{
    const isForm=options.body instanceof FormData;
    const headers=isForm?{...(options.headers||{})}:{"Content-Type":"application/json",...(options.headers||{})};
    const res=await fetch(BASE+"/api"+path,{...options,signal:controller.signal,headers});
    const text=await res.text();
    let data={};
    try{data=text?JSON.parse(text):{};}catch{data={message:text};}
    if(!res.ok) throw new Error(data.message||data.error||("Request failed ("+res.status+")"));
    return data;
  }catch(err){
    if(err.name==="AbortError") throw new Error("The JobSphere server did not respond within 90 seconds. Open the backend /api/health URL and check the Render service logs.");
    if(err instanceof TypeError) throw new Error("Unable to reach the JobSphere server. Check the API URL, backend service, and CORS settings.");
    throw err;
  }finally{clearTimeout(timer);}
}

export const api={
  health:()=>request("/health"),
  jobs:(search="")=>request("/jobs"+(search?"?search="+encodeURIComponent(search):"")),
  login:(body)=>request("/auth/login",{method:"POST",body:JSON.stringify(body)}),
  register:(body)=>request("/auth/register",{method:"POST",body:JSON.stringify(body)}),
  createJob:(body)=>request("/jobs",{method:"POST",body:JSON.stringify(body)}),
  applications:(candidateId)=>request("/applications/candidate/"+candidateId),
  recruiterApplications:(recruiterId)=>request("/applications/recruiter/"+recruiterId),
  apply:(formData)=>request("/applications",{method:"POST",body:formData}),
  updateApplicationStatus:(id,status)=>request("/applications/"+id+"/status?status="+encodeURIComponent(status),{method:"PATCH"}),
  resumeUrl:(id)=>BASE+"/api/applications/"+id+"/resume"
};
