const BASE=(import.meta.env.VITE_API_URL||"").replace(/\/$/,"");
async function request(path,options={}){
 const res=await fetch(BASE+"/api"+path,{headers:{"Content-Type":"application/json",...(options.headers||{})},...options});
 const data=await res.json().catch(()=>({}));
 if(!res.ok) throw new Error(data.message||data.error||data||"Request failed");
 return data;
}
export const api={
 jobs:(search="")=>request("/jobs"+(search?"?search="+encodeURIComponent(search):"")),
 login:(body)=>request("/auth/login",{method:"POST",body:JSON.stringify(body)}),
 register:(body)=>request("/auth/register",{method:"POST",body:JSON.stringify(body)}),
 createJob:(body)=>request("/jobs",{method:"POST",body:JSON.stringify(body)}),
 applications:(candidateId)=>request("/applications/candidate/"+candidateId),
 apply:(jobId,candidateId)=>request("/applications?jobId="+jobId+"&candidateId="+candidateId,{method:"POST"})
};
