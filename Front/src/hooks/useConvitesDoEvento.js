import { useState, useEffect } from 'react';
import { listarEnviadosADM } from '../services/conviteService';

export const useConvitesDoEvento = (eventoId) => {
  const [convites, setConvites] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    if (!eventoId) return;

    const carregar = async () => {
      try {
        setCarregando(true);
        const dados = await listarEnviadosADM();
        const filtrados = dados.filter(c => c.eventoId === Number(eventoId));
        setConvites(filtrados);
      } catch (err) {
        console.error('Erro ao carregar convites do evento:', err);
        setErro(err.data?.error || 'Erro ao carregar convites');
      } finally {
        setCarregando(false);
      }
    };

    carregar();
  }, [eventoId]); // só depende do id, sem funções instáveis

  return { convites, carregando, erro };
};