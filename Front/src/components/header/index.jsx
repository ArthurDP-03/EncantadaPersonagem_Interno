import "./index.css";
import { useState } from "react";
import { useNavigate, NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useTranslation } from "react-i18next";
import {
  House,
  CalendarDays,
  BarChart2,
  Handshake,
  Drama,
  Users,
  Bell,
  Mail,
  Moon,
  Sun,
  LogOut,
  Menu,
  X,
} from "lucide-react";

function Header({ dark, setDark }) {
  const { user, logout, hasRole } = useAuth();
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const currentLanguage = i18n.resolvedLanguage || i18n.language || "pt-BR";
  const isPortuguese = currentLanguage.startsWith("pt");

  function handleLogout() {
    logout();
    setMenuOpen(false);
    navigate("/login");
  }

  function toggleLanguage() {
    i18n.changeLanguage(isPortuguese ? "en-US" : "pt-BR");
  }

  const links =
    hasRole("ADMIN")
      ? [
          { label: t("nav.general"), href: "/", icone: <House size={18} /> },
          {
            label: t("nav.events"),
            href: "/eventos",
            icone: <CalendarDays size={18} />,
          },
          {
            label: t("nav.finance"),
            href: "/financeiro",
            icone: <BarChart2 size={18} />,
          },
          {
            label: t("nav.clients"),
            href: "/clientes",
            icone: <Handshake size={18} />,
          },
          {
            label: t("nav.characters"),
            href: "/personagens",
            icone: <Drama size={18} />,
          },
          {
            label: t("nav.collaborators"),
            href: "/colaboradores",
            icone: <Users size={18} />,
          },
        ]
      : [
          { label: t("nav.general"), href: "/", icone: <House size={18} /> },
          {
            label: "Eventos",
            href: "/eventos",
            icone: <CalendarDays size={18} />,
          },
          {
            label: "Convites",
            href: "/convites",
            icone: <Mail size={18} />,
          },
        ];

  if (!user) return null;

  return (
    <header className="cabecalho">
      <section className="cabecalho-section">
        <div className="conteudo-95">
          <div className="conteudo">
            <div className="perfil">
              <div className="imagem-container">
                <img src="" alt="" className="foto_perfil" />
              </div>

              <div className="textos">
                <div className="nome">
                  <p>{user.name}</p>
                </div>
                <div className="cargo">
                  <p>
                    {hasRole("ADMIN")
                      ? t("common.roles.admin")
                      : t("common.roles.actor")}
                  </p>
                </div>
              </div>
            </div>

            <div className="logo">
              <div className="imagem-container">
                <img src="src/assets/logo.png" alt="" />
              </div>
            </div>

            <div className="funcionalidades">
              <div className="icone" onClick={() => setDark((d) => !d)}>
                {dark ? <Sun size={30} /> : <Moon size={30} />}
              </div>
              <button
                type="button"
                className="icone language-toggle"
                onClick={toggleLanguage}
              >
                <span>{isPortuguese ? t("common.languages.pt") : t("common.languages.en")}</span>
              </button>
              <div className="icone">
                <Bell size={30} />
              </div>
              <button
                type="button"
                className={`mobile-menu-toggle ${menuOpen ? "open" : ""}`}
                onClick={() => setMenuOpen((prev) => !prev)}
                aria-label={menuOpen ? t("nav.closeMenu") : t("nav.openMenu")}
              >
                {menuOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
            </div>
          </div>
        </div>
      </section>

      <section className={`mobile-navigation ${menuOpen ? "open" : ""}`} aria-hidden={!menuOpen}>
        <div className="mobile-menu">
          <div className="mobile-menu-header">
            <span>{t("nav.menu")}</span>
          </div>
          <div className="mobile-menu-links">
            {links.map(({ label, href, icone }) => (
              <NavLink
                key={href}
                to={href}
                className={({ isActive }) => `link ${isActive ? "ativo" : ""}`}
                onClick={() => setMenuOpen(false)}
              >
                <div className="texto">
                  <p>{label}</p>
                </div>
                <div className="icone">{icone}</div>
              </NavLink>
            ))}
            <button onClick={handleLogout} className="link logout">
              <div className="texto">
                <p>{t("nav.logout")}</p>
              </div>
              <div className="icone">
                <LogOut size={18} />
              </div>
            </button>
          </div>
        </div>
      </section>

      <section className="navegacao">
        <div className="lista-link">
          {links.map(({ label, href, icone }) => (
            <NavLink
              key={href}
              to={href}
              className={({ isActive }) => `link ${isActive ? "ativo" : ""}`}
            >
              <div className="texto">
                <p>{label}</p>
              </div>
              <div className="icone">{icone}</div>
            </NavLink>
          ))}
        </div>

        <div className="lista-link">
          <button onClick={handleLogout} className="link logout">
            <div className="texto">
              <p>{t("nav.logout")}</p>
            </div>
            <div className="icone">
              <LogOut size={18} />
            </div>
          </button>
        </div>
      </section>
    </header>
  );
}

export default Header;
