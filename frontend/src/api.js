const BASE=(import.meta.env.VITE_API_URL||"").replace(/\/$/,"");

async function request(path,options={}){
  const controller=new AbortController();
  const timer=setTimeout(()=>controller.abort(),15000);
  try{
    const res=await fetch(BASE+"/api"+path,{
      ...options,
      signal:controller.signal,
      headers:{"Content-Type":"application/json",...(options.headers||{})}
    });
    const text=await res.text();
    let data={};
    try{data=text?JSON.parse(text):{};}catch{data={message:text};}
    if(!res.ok) throw new Error(data.message||data.error||("Request failed ("+res.status+")"));
    return data;
  }catch(err){
    if(err.name==="AbortError") throw new Error("The server took too long to respond. Please try again.");
    if(err instanceof TypeError) throw new Error("Unable to reach the JobSphere server. Check the API URL and backend deployment.");
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
  apply:(jobId,candidateId)=>request("/applications?jobId="+jobId+"&candidateId="+candidateId,{method:"POST"})
};
