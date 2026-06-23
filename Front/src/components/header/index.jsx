import "./index.css";
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
  Moon,
  Sun,
  LogOut,
} from "lucide-react";

function Header({ dark, setDark }) {
  const { user, logout } = useAuth();
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const currentLanguage = i18n.resolvedLanguage || i18n.language || "pt-BR";
  const isPortuguese = currentLanguage.startsWith("pt");

  function handleLogout() {
    logout();
    navigate("/login");
  }

  function toggleLanguage() {
    i18n.changeLanguage(isPortuguese ? "en-US" : "pt-BR");
  }

  const links =
    user?.role === "ADMIN"
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
            label: t("nav.events"),
            href: "/eventos",
            icone: <CalendarDays size={18} />,
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
                    {user.role === "ADMIN"
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
            </div>
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
