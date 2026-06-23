import React, { useMemo, useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { 
  LayoutDashboard, Users, UserSquare2, CalendarDays, 
  ClipboardList, Drama, Mail, CalendarHeart 
} from 'lucide-react';
import CardAtalho from '../card_atalho';
import { useAuth } from '../../context/AuthContext';
import './index.css';

const Home = () => {
  const { user, hasRole } = useAuth();
  const { t } = useTranslation();
  
  const [displayedText, setDisplayedText] = useState('');

  const userName = useMemo(() => {
    if (user?.name) return user.name;
    
    if (user?.sub) {
      const emailName = user.sub.split('@')[0];
      return emailName.charAt(0).toUpperCase() + emailName.slice(1);
    }
    return 'Usuário';
  }, [user]);

  const greetingText = t('home.welcome'); 
  const nameText = `${userName}!`;
  const fullText = greetingText + nameText;

  useEffect(() => {
    if (!fullText) return;
    
    let i = 0;
    setDisplayedText('');
    
    const typingInterval = setInterval(() => {
      setDisplayedText(fullText.substring(0, i + 1));
      i++;
      if (i >= fullText.length) {
        clearInterval(typingInterval);
      }
    }, 45); 

    return () => clearInterval(typingInterval);
  }, [fullText]);

  const userShortcuts = useMemo(() => {
    const adminCards = [
      { title: t('home.cards.dashboard.title'), description: t('home.cards.dashboard.description'), icon: LayoutDashboard, path: '/financeiro' },
      { title: t('home.cards.colaboradores.title'), description: t('home.cards.colaboradores.description'), icon: Users, path: '/colaboradores' },
      { title: t('home.cards.clientes.title'), description: t('home.cards.clientes.description'), icon: UserSquare2, path: '/clientes' },
      { title: t('home.cards.eventos.title'), description: t('home.cards.eventos.description'), icon: CalendarDays, path: '/eventos' },
      { title: t('home.cards.personagens.title'), description: t('home.cards.personagens.description'), icon: Drama, path: '/personagens' },
    ];

    const atorCards = [
      { title: t('home.cards.meus_eventos.title'), description: t('home.cards.meus_eventos.description'), icon: CalendarHeart, path: '/eventos' },
      { title: t('home.cards.minha_agenda.title'), description: t('home.cards.minha_agenda.description'), icon: ClipboardList, path: '/eventos' },
      { title: t('home.cards.responder_convites.title'), description: t('home.cards.responder_convites.description'), icon: Mail, path: '/convites' },
    ];

    return hasRole('ADMIN') ? adminCards : atorCards;
  }, [hasRole, t]);

  if (!user) return null;

  return (
    <section className="home-container">
      <header className="home-header">
        <h1>
          {displayedText.length > greetingText.length ? greetingText : displayedText}
          <span>
            {displayedText.length > greetingText.length ? displayedText.slice(greetingText.length) : ''}
          </span>
        </h1>
        <p className="animate-fade-in delay-subtitle">{t('home.question')}</p>
      </header>

      <div className="cards-wrapper">
        {userShortcuts.map((card, index) => (
          <div 
            key={`${card.path}-${index}`} 
            className="animate-fade-in-card"
            style={{ animationDelay: `${0.4 + (index * 0.15)}s` }}
          >
            <CardAtalho 
              title={card.title}
              description={card.description}
              icon={card.icon}
              path={card.path}
            />
          </div>
        ))}
      </div>
    </section>
  );
};

export default Home;
