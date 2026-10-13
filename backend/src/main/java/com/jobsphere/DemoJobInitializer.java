package com.jobsphere;

import com.jobsphere.model.Job;
import com.jobsphere.model.Role;
import com.jobsphere.model.User;
import com.jobsphere.repository.JobRepository;
import com.jobsphere.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Idempotently adds a small catalogue of real listings found on official company
 * career sites. It never deletes or resets user data and only assigns listings
 * to the existing padma@gmail.com recruiter account; it never creates or changes
 * account credentials.
 */
@Configuration
public class DemoJobInitializer {
    private static final Logger log = LoggerFactory.getLogger(DemoJobInitializer.class);
    private static final String RECRUITER_EMAIL = "padma@gmail.com";
    private static final String SOURCE = "Official Company Careers";

    private record Listing(
            String title, String company, String location, String level, String type,
            String salary, String skills, String description, String sourceUrl) {}

    @Bean
    CommandLineRunner seedOfficialJobs(UserRepository users, JobRepository jobs) {
        return args -> {
            User recruiter = users.findByEmail(RECRUITER_EMAIL).orElse(null);
            if (recruiter == null || recruiter.getRole() != Role.RECRUITER) {
                log.warn("Official job listings were not seeded: the existing recruiter account {} was not found.", RECRUITER_EMAIL);
                return;
            }

            int added = 0;
            for (Listing listing : listings()) {
                if (jobs.existsByCompanyIgnoreCaseAndTitleIgnoreCase(listing.company(), listing.title())) {
                    continue;
                }
                Job job = new Job();
                job.setTitle(listing.title());
                job.setCompany(listing.company());
                job.setLocation(listing.location());
                job.setType(listing.type());
                job.setLevel(listing.level());
                job.setSalary(listing.salary());
                job.setSkills(listing.skills());
                job.setDescription(listing.description());
                job.setEligibilityCriteria("Review the official company listing for the current requirements and eligibility.");
                job.setSourceName(SOURCE);
                job.setSourceUrl(listing.sourceUrl());
                job.setRecruiter(recruiter);
                job.setActive(true);
                jobs.save(job);
                added++;
            }
            log.info("Official career listings: added {} new jobs for recruiter {}.", added, RECRUITER_EMAIL);
        };
    }

    private List<Listing> listings() {
        String amazonIndia = "https://www.amazon.jobs/en/search?base_query=software&country=IND&loc_query=India";
        String amazonHyderabad = "https://www.amazon.jobs/en/search?city=Hyderabad&country=IND&loc_query=Hyderabad%2C+TS%2C+India";
        String microsoftIndia = "https://careers.microsoft.com/v2/global/en/locations/india.html";
        String microsoftHyderabad = "https://careers.microsoft.com/v2/global/en/locations/hyderabad.html";
        String microsoftBengaluru = "https://careers.microsoft.com/v2/global/en/locations/bengaluru.html";

        return List.of(
            new Listing("Software Development Engineer II, Timehub Experience", "Amazon", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Java, software design, distributed systems, testing", "Build and maintain software for Amazon's Timehub Experience. Review the official listing for exact qualifications and responsibilities.", "https://www.amazon.jobs/en/jobs/10561009"),
            new Listing("Software Development Engineer, Time & Pay Innovation", "Amazon", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Java, C++, object-oriented design, distributed systems", "Engineering role on Amazon's Time & Pay Innovation team. Check the official posting for the complete requirements.", "https://www.amazon.jobs/en/jobs/10561007"),
            new Listing("Software Development Engineer II", "Amazon", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Software engineering, system design, coding, testing", "Software development role listed for Hyderabad. Review the official posting for current responsibilities and eligibility.", "https://www.amazon.jobs/en/jobs/10555781"),
            new Listing("Software Development Engineer, Hyderabad", "Amazon", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Programming, algorithms, software development lifecycle", "Software Development Engineer opening listed for Hyderabad. Confirm the latest requirements on Amazon Jobs.", "https://www.amazon.jobs/en/jobs/10555143"),
            new Listing("Sr. Software Dev Engineer, Amazon Freight", "Amazon", "Hyderabad, Telangana, India", "Senior", "Full-time", "See official listing", "Software architecture, distributed systems, technical leadership", "Senior engineering role with Amazon Freight. This listing is intended for experienced engineers; see the official posting for detailed requirements.", "https://www.amazon.jobs/en/jobs/10575729"),
            new Listing("Software Development Engineer, Device Software Services", "Amazon", "Bengaluru, Karnataka, India", "Experienced", "Full-time", "See official listing", "Software development, architecture, coding, testing", "Device Software Services engineering opening listed on Amazon Jobs. Check the official page for exact qualifications.", amazonIndia),
            new Listing("Software Dev Engineer II, Emerging Device Software", "Amazon", "Bengaluru, Karnataka, India", "Experienced", "Full-time", "See official listing", "Android, software engineering, design patterns, testing", "Engineering role for Emerging Device Software. Consult the official listing for the complete experience requirements.", amazonIndia),
            new Listing("Software Development Engineer, Video Games", "Amazon", "Bengaluru, Karnataka, India", "Experienced", "Full-time", "See official listing", "Programming, software development, release and live operations", "Software engineering role supporting video game development, release, or live operations. Verify details on the official careers page.", "https://www.amazon.jobs/en/jobs/10571786"),
            new Listing("Software Development Engineer, Game Live Operations", "Amazon", "Bengaluru, Karnataka, India", "Experienced", "Full-time", "See official listing", "Programming, software engineering, live operations", "A separate Software Development Engineer posting in Amazon's games area. Confirm the exact team and requirements in the official listing.", "https://www.amazon.jobs/en/jobs/10571791"),
            new Listing("Software Development Engineer II, Timehub Pay Extract", "Amazon", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Java, system design, distributed systems, testing", "Engineering opening on Timehub Pay Extract. Review the official posting for exact qualifications.", "https://www.amazon.jobs/en/jobs/10562899"),
            new Listing("Support Engineer, EPR PayOnBehalf", "Amazon", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Troubleshooting, debugging, scripting, technical support", "Technical support engineering role listed for Hyderabad. Check Amazon Jobs for full eligibility and responsibilities.", "https://www.amazon.jobs/en/jobs/10558927"),
            new Listing("Support Engineer III, Chronicle", "Amazon", "Chennai, Tamil Nadu, India", "Experienced", "Full-time", "See official listing", "Scripting, troubleshooting, debugging, systems support", "Support engineering role listed for Chennai. Consult the official posting for the required experience.", "https://www.amazon.jobs/en/jobs/10537541"),
            new Listing("IT Support Engineer I", "Amazon", "Bengaluru, Karnataka, India", "Entry to mid-level", "Full-time", "See official listing", "Windows Server, networking, DNS, TCP/IP, Linux", "IT support role covering infrastructure and troubleshooting. Review the official listing for experience and location details.", "https://www.amazon.jobs/en/jobs/10482407"),

            new Listing("Service Engineer II", "Microsoft", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Full-stack tools, automation, APIs, troubleshooting", "Microsoft Advertising service engineering role focused on automation tools, investigation, and service reliability. See the official page for full details.", microsoftHyderabad),
            new Listing("Software Engineer II, Azure Data", "Microsoft", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Java, C#, Python, data platforms, distributed systems", "Software engineering role in Microsoft's Azure Data organisation. Review the official listing for specific team and qualification details.", microsoftHyderabad),
            new Listing("Software Engineer II (DevOps)", "Microsoft", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Azure DevOps, CI/CD, Python, PowerShell, Git", "DevOps-focused engineering role involving deployment automation, cloud infrastructure, monitoring, and troubleshooting. Check the official page for exact requirements.", microsoftHyderabad),
            new Listing("Senior Software Engineer, AI Labs", "Microsoft", "Hyderabad, Telangana, India", "Senior", "Full-time", "See official listing", "Software engineering, AI-enabled applications, prototyping, system design", "Senior software engineering opportunity associated with Microsoft's AI Labs. Verify current details and requirements on the official careers site.", microsoftIndia),
            new Listing("Software Engineer II, Azure Data", "Microsoft", "Bengaluru, Karnataka, India", "Experienced", "Full-time", "See official listing", "Data engineering, distributed systems, software development", "Software Engineer II listing in Microsoft's Azure Data area for Bengaluru. Consult the official posting for requirements.", microsoftBengaluru),
            new Listing("Software Engineer II, Microsoft Defender Experts", "Microsoft", "Hyderabad, Telangana, India", "Experienced", "Full-time", "See official listing", "Security engineering, automation, AI, cloud services", "Software engineering opportunity supporting managed security services with AI and automation. Check Microsoft's official page for full qualifications.", microsoftIndia),
            new Listing("Digital Account Management IC5", "Microsoft", "Mumbai, Maharashtra, India", "Experienced", "Full-time", "See official listing", "Customer engagement, account research, stakeholder management", "Digital account management opportunity listed for Mumbai. Review the official Microsoft careers page for role requirements.", microsoftIndia)
        );
    }
}
