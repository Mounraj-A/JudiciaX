import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { ROUTES } from '@/constants/routes';
import {
  Scale,
  Globe,
  LogIn,
  Sparkles,
  Search,
  Landmark,
  BrainCircuit,
  FilePlus2,
  CheckCircle2, // or CheckCircle
  Users,
  FileText,
  CalendarDays,
  Gavel,
  Wallet,
  CopyCheck,
  ShieldCheck,
  ArrowRight,
  Eye,
  FolderCheck,
  Zap,
  ChevronDown,
  MapPin,
  Phone,
  Mail,
  MessageSquare,
  Map as MapIcon
} from 'lucide-react';
import './LandingPage.css';

// ── Smooth-scroll helper ────────────────────────────────────────────────────
function scrollToSection(hash: string) {
  if (!hash) return;
  const id = hash.replace('#', '');
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

export function LandingPage() {
  const navigate = useNavigate();
  const location = useLocation();

  const [activeFaq, setActiveFaq] = useState<number | null>(0);
  const [activeNav, setActiveNav] = useState<string>('home');

  // Handle hash-based scroll on mount or hash change
  useEffect(() => {
    if (location.hash) {
      const timer = setTimeout(() => scrollToSection(location.hash), 100);
      return () => clearTimeout(timer);
    } else {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [location.hash]);

  const toggleFaq = (index: number) => {
    setActiveFaq(activeFaq === index ? null : index);
  };

  return (
    <div className="landing-page-wrapper">
      {/* ===== NAVBAR ===== */}
      <header className="navbar">
        <div className="wrap">
          <div className="brand">
            <div className="brand-mark"><Scale size={22} /></div>
            <div>
              <div className="brand-name">JudiciaX</div>
              <div className="brand-sub">Judicial Case Management</div>
            </div>
          </div>
          <nav className="menu">
            <button className={activeNav === 'home' ? 'active' : ''} onClick={() => { setActiveNav('home'); window.scrollTo({ top: 0, behavior: 'smooth' }); }}>Home</button>
            <button className={activeNav === 'about' ? 'active' : ''} onClick={() => { setActiveNav('about'); scrollToSection('#about'); }}>About</button>
            <button className={activeNav === 'services' ? 'active' : ''} onClick={() => { setActiveNav('services'); scrollToSection('#services'); }}>Services</button>
            <button className={activeNav === 'track-case' ? 'active' : ''} onClick={() => { setActiveNav('track-case'); scrollToSection('#track-case'); }}>Track case</button>
            <button className={activeNav === 'announcements' ? 'active' : ''} onClick={() => { setActiveNav('announcements'); scrollToSection('#announcements'); }}>Announcements</button>
            <button className={activeNav === 'faq' ? 'active' : ''} onClick={() => { setActiveNav('faq'); scrollToSection('#faq'); }}>FAQ</button>
            <button className={activeNav === 'contact' ? 'active' : ''} onClick={() => { setActiveNav('contact'); scrollToSection('#contact'); }}>Contact</button>
          </nav>
          <div className="nav-right">
            <div className="lang-select"><Globe size={16} /> English</div>
            <button className="btn btn-primary" onClick={() => navigate(ROUTES.AUTH.LOGIN)}>
              <LogIn size={16} /> Login
            </button>
          </div>
        </div>
      </header>

      {/* ===== HERO ===== */}
      <section className="hero" id="about">
        <div className="wrap">
          <div className="hero-copy">
            <div className="hero-eyebrow"><Sparkles size={14} /> AI-powered case prioritization</div>
            <h1>Justice.<br />Transparency.<br /><span>Efficiency.</span></h1>
            <p className="lead">JudiciaX brings every stage of a case, from filing to judgment, onto one secure digital platform, using AI to prioritize hearings fairly and keep citizens informed at every step.</p>
            <div className="hero-actions">
              <button className="btn btn-primary" onClick={() => scrollToSection('#track-case')}>
                <Search size={16} /> Track your case
              </button>
              <button className="btn btn-outline-white" onClick={() => scrollToSection('#services')}>Learn more</button>
            </div>
            <div className="hero-meta">
              <div><span className="num">18.4L+</span><span className="lbl">Cases filed</span></div>
              <div><span className="num">14.9L+</span><span className="lbl">Cases disposed</span></div>
              <div><span className="num">412</span><span className="lbl">Courts connected</span></div>
              <div><span className="num">6.2L+</span><span className="lbl">Registered users</span></div>
            </div>
          </div>
          <div className="hero-visual">
            <div className="panel">
              <div className="panel-top">
                <span className="panel-title">Case intelligence overview</span>
                <span className="live"><span className="live-dot"></span>Live</span>
              </div>
              <div className="courthouse-icon">
                <div className="ai-badge"><BrainCircuit size={12} /> AI active</div>
                <Landmark size={88} />
              </div>
              <div className="stat-row">
                <div className="stat-chip"><div className="n">2,340</div><div className="l">Hearings today</div></div>
                <div className="stat-chip"><div className="n">98.2%</div><div className="l">SLA compliance</div></div>
                <div className="stat-chip"><div className="n">6 min</div><div className="l">Avg. lookup time</div></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== KPI / TRUST ===== */}
      <section className="kpi-section">
        <div className="wrap">
          <div className="kpi-grid">
            <div className="kpi-card">
              <div className="kpi-icon blue"><FilePlus2 size={24} /></div>
              <div><div className="n">18,42,650</div><div className="l">Cases filed</div></div>
            </div>
            <div className="kpi-card">
              <div className="kpi-icon green"><CheckCircle2 size={24} /></div>
              <div><div className="n">14,96,210</div><div className="l">Cases disposed</div></div>
            </div>
            <div className="kpi-card">
              <div className="kpi-icon orange"><Landmark size={24} /></div>
              <div><div className="n">412</div><div className="l">Courts connected</div></div>
            </div>
            <div className="kpi-card">
              <div className="kpi-icon navy"><Users size={24} /></div>
              <div><div className="n">6,21,304</div><div className="l">Registered users</div></div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== SERVICES ===== */}
      <section className="section" id="services">
        <div className="wrap">
          <div className="section-head">
            <div className="eyebrow">Citizen services</div>
            <h2>Everything you need, in one portal</h2>
            <p>Digital services covering the complete case lifecycle, available any time, from anywhere.</p>
          </div>
          <div className="services-grid">
            <div className="service-card">
              <div className="service-icon"><FileText size={24} /></div>
              <h4>Case filing</h4>
              <p>File new cases digitally with guided document checklists and instant acknowledgement.</p>
              <div className="service-link">Get started <ArrowRight size={14} /></div>
            </div>
            <div className="service-card" onClick={() => scrollToSection('#track-case')}>
              <div className="service-icon"><Search size={24} /></div>
              <h4>Track case</h4>
              <p>Follow your case status, hearing dates, and orders in real time using your case number.</p>
              <div className="service-link">Track now <ArrowRight size={14} /></div>
            </div>
            <div className="service-card">
              <div className="service-icon"><CalendarDays size={24} /></div>
              <h4>Cause list</h4>
              <p>View daily and weekly cause lists for every connected court, sorted by bench and time.</p>
              <div className="service-link">View list <ArrowRight size={14} /></div>
            </div>
            <div className="service-card">
              <div className="service-icon"><Gavel size={24} /></div>
              <h4>Orders and judgments</h4>
              <p>Search and download certified digital copies of orders and judgments by case or court.</p>
              <div className="service-link">Search records <ArrowRight size={14} /></div>
            </div>
            <div className="service-card">
              <div className="service-icon"><Wallet size={24} /></div>
              <h4>Court fees</h4>
              <p>Calculate and pay court fees securely online with instant digital receipts.</p>
              <div className="service-link">Pay fees <ArrowRight size={14} /></div>
            </div>
            <div className="service-card">
              <div className="service-icon"><CopyCheck size={24} /></div>
              <h4>Certified copies</h4>
              <p>Request certified copies of case documents with tracked delivery status.</p>
              <div className="service-link">Request copy <ArrowRight size={14} /></div>
            </div>
            <div className="service-card">
              <div className="service-icon"><ShieldCheck size={24} /></div>
              <h4>Digital services</h4>
              <p>Access e-filing, e-payments, and verified digital document lockers in one place.</p>
              <div className="service-link">Explore <ArrowRight size={14} /></div>
            </div>
            <div className="service-card" onClick={() => navigate(ROUTES.AUTH.LOGIN)}>
              <div className="service-icon"><Scale size={24} /></div>
              <h4>Advocate access</h4>
              <p>Advocates get case dashboards, e-filing tools, and cause list alerts on login.</p>
              <div className="service-link">Advocate login <ArrowRight size={14} /></div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== HOW IT WORKS ===== */}
      <section className="section" style={{ background: 'var(--surface)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)' }}>
        <div className="wrap">
          <div className="section-head center">
            <div className="eyebrow" style={{ justifyContent: 'center' }}>Process</div>
            <h2>How a case moves through JudiciaX</h2>
            <p>A transparent, sequential path from filing to final judgment.</p>
          </div>
          <div className="timeline-wrap">
            <div className="timeline-track"></div>
            <div className="timeline">
              <div className="t-step"><div className="t-dot">1</div><h5>Case filed</h5><p>Citizen submits case online</p></div>
              <div className="t-step"><div className="t-dot">2</div><h5>Documents uploaded</h5><p>Supporting files attached</p></div>
              <div className="t-step"><div className="t-dot">3</div><h5>Verification</h5><p>Registry checks completeness</p></div>
              <div className="t-step"><div className="t-dot">4</div><h5>Case registered</h5><p>Case number allotted</p></div>
              <div className="t-step"><div className="t-dot">5</div><h5>Judge assigned</h5><p>AI-assisted bench allocation</p></div>
              <div className="t-step"><div className="t-dot">6</div><h5>Hearing</h5><p>Scheduled and notified</p></div>
              <div className="t-step"><div className="t-dot">7</div><h5>Judgment</h5><p>Order delivered digitally</p></div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== ANNOUNCEMENTS ===== */}
      <section className="section" id="announcements">
        <div className="wrap">
          <div className="section-head" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', maxWidth: 'none' }}>
            <div>
              <div className="eyebrow">Stay informed</div>
              <h2 style={{ marginBottom: 0 }}>Latest announcements</h2>
            </div>
            <button className="btn btn-secondary">View all <ArrowRight size={15} /></button>
          </div>
          <div className="ann-grid">
            <div className="ann-card">
              <div className="ann-top"><span className="tag notice">Notice</span><span className="ann-date">14 Jul 2026</span></div>
              <h4>Revised cause list timings for district courts effective 1 August</h4>
              <div className="ann-more">Read more <ArrowRight size={13} /></div>
            </div>
            <div className="ann-card">
              <div className="ann-top"><span className="tag circular">Circular</span><span className="ann-date">10 Jul 2026</span></div>
              <h4>E-filing mandatory for civil suits above threshold value</h4>
              <div className="ann-more">Read more <ArrowRight size={13} /></div>
            </div>
            <div className="ann-card">
              <div className="ann-top"><span className="tag judgment">Judgment</span><span className="ann-date">6 Jul 2026</span></div>
              <h4>Landmark ruling on digital evidence admissibility published</h4>
              <div className="ann-more">Read more <ArrowRight size={13} /></div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== TRACK CASE ===== */}
      <section className="section" id="track-case" style={{ paddingTop: '8px' }}>
        <div className="wrap">
          <div className="track-section">
            <div className="track-grid">
              <div>
                <h2>Track your case in seconds</h2>
                <p>Enter your case number to view live status, next hearing date, and the latest orders.</p>
                <div className="recent-tags">
                  <span>CIV/2026/04812</span><span>CRL/2025/91023</span><span>WP/2026/00567</span>
                </div>
              </div>
              <div className="track-form">
                <input type="text" placeholder="Enter case number, e.g. CIV/2026/04812" />
                <button className="btn btn-primary" style={{ height: '50px', whiteSpace: 'nowrap' }}>Track case</button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ===== WHY CHOOSE JUDICIAX ===== */}
      <section className="section">
        <div className="wrap">
          <div className="section-head center">
            <div className="eyebrow" style={{ justifyContent: 'center' }}>Why JudiciaX</div>
            <h2>Built for trust, speed, and access</h2>
          </div>
          <div className="why-grid">
            <div className="why-card">
              <div className="why-icon" style={{ background: '#FFEDD5', color: 'var(--orange)' }}><BrainCircuit size={24} /></div>
              <h4>AI prioritization</h4>
              <p>Cases are ranked fairly using AI models trained on urgency and statutory timelines.</p>
            </div>
            <div className="why-card">
              <div className="why-icon" style={{ background: '#DBEAFE', color: 'var(--royal)' }}><ShieldCheck size={24} /></div>
              <h4>Secure</h4>
              <p>End-to-end encryption and role-based access protect every case record.</p>
            </div>
            <div className="why-card">
              <div className="why-icon" style={{ background: '#DCFCE7', color: 'var(--success)' }}><Eye size={24} /></div>
              <h4>Transparent</h4>
              <p>Every status update and order is visible to the concerned parties in real time.</p>
            </div>
            <div className="why-card">
              <div className="why-icon" style={{ background: '#E2E8F0', color: 'var(--navy)' }}><FolderCheck size={24} /></div>
              <h4>Digital documents</h4>
              <p>Certified digital copies and e-filing remove paper dependency end to end.</p>
            </div>
            <div className="why-card">
              <div className="why-icon" style={{ background: '#FEF3C7', color: '#B45309' }}><Zap size={24} /></div>
              <h4>Fast processing</h4>
              <p>Automated verification cuts registration time from weeks to days.</p>
            </div>
          </div>
        </div>
      </section>

      {/* ===== FAQ ===== */}
      <section className="section" id="faq" style={{ background: 'var(--surface)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)' }}>
        <div className="wrap">
          <div className="section-head center">
            <div className="eyebrow" style={{ justifyContent: 'center' }}>Support</div>
            <h2>Frequently asked questions</h2>
          </div>
          <div className="faq-wrap">
            {[
              {
                q: 'How do I track my case status?',
                a: 'Use the case number provided at filing in the Track case section. Status, next hearing date, and recent orders are updated as soon as the court records them.'
              },
              {
                q: 'Who can file a case on JudiciaX?',
                a: 'Citizens can file eligible case types directly, and advocates can file on behalf of clients after logging into their advocate account.'
              },
              {
                q: 'Is my data secure on this platform?',
                a: 'All records are encrypted in transit and at rest, with access limited by role, so only authorized parties can view case details.'
              },
              {
                q: 'How does AI prioritization decide hearing order?',
                a: 'The system ranks pending matters using statutory deadlines, case age, and urgency indicators, then presents the ranked list to the assigned judge for final scheduling.'
              }
            ].map((faq, index) => (
              <div key={index} className={`faq-item ${activeFaq === index ? 'open' : ''}`}>
                <div className="faq-q" onClick={() => toggleFaq(index)}>
                  {faq.q} <ChevronDown className="chev" size={18} />
                </div>
                <div className="faq-a">{faq.a}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ===== CONTACT ===== */}
      <section className="section" id="contact">
        <div className="wrap">
          <div className="section-head">
            <div className="eyebrow">Get in touch</div>
            <h2>Contact the registry</h2>
          </div>
          <div className="contact-grid">
            <div className="contact-info">
              <div className="info-row">
                <div className="info-icon"><MapPin size={24} /></div>
                <div><h5>Registry office</h5><p>High Court Complex, Court Road, Salem, Tamil Nadu 636001</p></div>
              </div>
              <div className="info-row">
                <div className="info-icon"><Phone size={24} /></div>
                <div><h5>Helpline</h5><p>1800 425 0110 (toll free, 9 am – 6 pm)</p></div>
              </div>
              <div className="info-row">
                <div className="info-icon"><Mail size={24} /></div>
                <div><h5>Email support</h5><p>support@judiciax.gov.in</p></div>
              </div>
              <button className="btn btn-primary" style={{ marginTop: '8px', alignSelf: 'flex-start' }}>
                <MessageSquare size={16} /> Contact registry
              </button>
            </div>
            <div className="map-placeholder">
              <MapIcon size={32} />
              <span style={{ fontSize: '13px' }}>Map placeholder — court location</span>
            </div>
          </div>
        </div>
      </section>

      {/* ===== FOOTER ===== */}
      <footer>
        <div className="wrap">
          <div className="footer-grid">
            <div className="footer-brand">
              <div className="brand" style={{ marginBottom: 0 }}>
                <div className="brand-mark" style={{ background: 'rgba(255,255,255,0.08)', color: 'var(--orange)' }}>
                  <Scale size={22} />
                </div>
                <div className="brand-name">JudiciaX</div>
              </div>
              <p>An AI-powered judicial case management platform connecting citizens, advocates, and courts on one trusted digital system.</p>
            </div>
            <div className="footer-col">
              <h5>Quick links</h5>
              <button onClick={() => scrollToSection('#about')} style={{ background: 'none', border: 'none', color: 'inherit', padding: 0, textAlign: 'left', cursor: 'pointer' }}>About JudiciaX</button>
              <button onClick={() => scrollToSection('#services')} style={{ background: 'none', border: 'none', color: 'inherit', padding: 0, textAlign: 'left', cursor: 'pointer', display: 'block', marginTop: '12px' }}>Services</button>
              <button onClick={() => scrollToSection('#announcements')} style={{ background: 'none', border: 'none', color: 'inherit', padding: 0, textAlign: 'left', cursor: 'pointer', display: 'block', marginTop: '12px' }}>Announcements</button>
              <button onClick={() => scrollToSection('#contact')} style={{ background: 'none', border: 'none', color: 'inherit', padding: 0, textAlign: 'left', cursor: 'pointer', display: 'block', marginTop: '12px' }}>Contact</button>
            </div>
            <div className="footer-col">
              <h5>Citizen services</h5>
              <a href="#">Case filing</a>
              <a href="#">Track case</a>
              <a href="#">Cause list</a>
              <a href="#">Certified copies</a>
            </div>
            <div className="footer-col">
              <h5>Legal</h5>
              <a href="#">Privacy policy</a>
              <a href="#">Terms of use</a>
              <a href="#">Accessibility statement</a>
            </div>
          </div>
          <div className="footer-bottom">
            <span>© {new Date().getFullYear()} JudiciaX. All rights reserved.</span>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default LandingPage;
