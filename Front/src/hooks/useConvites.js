import { useState } from 'react';
import Swal from 'sweetalert2';
import i18n from '../i18n';
import {
  enviarConvites,
  listarPorEventoPersonagem,
  listarEnviadosADM,
  listarMeusConvites,
  responderConvite,
  cancelarConvite,
  ConviteStatus
} from '../services/conviteService';

const t = i18n.t.bind(i18n);

export const useConvite = () => {
  const [convites, setConvites] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const listarPorEvento = async (epId) => {
    try {
      setCarregando(true);
      const dados = await listarPorEventoPersonagem(epId);
      setConvites(dados);
      return dados;
    } catch (err) {
      console.error('Erro ao listar convites:', err);
      setErro(err.data?.error || t('invites.errors.load'));
      Swal.fire({
        icon: 'error',
        title: t('invites.titles.load'),
        text: err.data?.error || t('invites.errors.load'),
      });
    } finally {
      setCarregando(false);
    }
  };

  const listarEnviados = async () => {
    try {
      setCarregando(true);
      const dados = await listarEnviadosADM();
      setConvites(dados);
      return dados;
    } catch (err) {
      console.error('Erro ao listar convites enviados:', err);
      setErro(err.data?.error || t('invites.errors.loadSent'));
      Swal.fire({
        icon: 'error',
        title: t('invites.titles.load'),
        text: err.data?.error || t('invites.errors.loadSent'),
      });
    } finally {
      setCarregando(false);
    }
  };

  const listarMeus = async () => {
    try {
      setCarregando(true);
      const dados = await listarMeusConvites();
      setConvites(dados);
      return dados;
    } catch (err) {
      console.error('Erro ao listar meus convites:', err);
      setErro(err.data?.error || t('invites.errors.loadMine'));
      Swal.fire({
        icon: 'error',
        title: t('invites.titles.load'),
        text: err.data?.error || t('invites.errors.loadMine'),
      });
    } finally {
      setCarregando(false);
    }
  };

  const enviar = async (eventoPersonagemId, atoresIds) => {
    try {
      const novosConvites = await enviarConvites({ eventoPersonagemId, atoresIds });
      setConvites(prev => [...prev, ...novosConvites]);
      Swal.fire({
        icon: 'success',
        title: t('invites.success.sent'),
        timer: 1800,
        showConfirmButton: false
      });
      return novosConvites;
    } catch (err) {
      console.error('Erro ao enviar convites:', err);
      Swal.fire({
        icon: 'error',
        title: t('invites.titles.send'),
        text: err.data?.error || t('invites.errors.send'),
      });
    }
  };

  const responder = async (conviteId, status) => {
    try {
      const atualizado = await responderConvite(conviteId, { status });
      setConvites(prev => prev.map(c => (c.id === conviteId ? atualizado : c)));
      
      const mensagem = status === ConviteStatus.ACEITO ? t('invites.success.accepted') : t('invites.success.declined');
      Swal.fire({
        icon: 'success',
        title: mensagem,
        timer: 1800,
        showConfirmButton: false
      });
      return atualizado;
    } catch (err) {
      console.error('Erro ao responder convite:', err);
      Swal.fire({
        icon: 'error',
        title: t('invites.titles.respond'),
        text: err.data?.error || t('invites.errors.respond'),
      });
    }
  };

  const cancelar = async (conviteId) => {
    const resultado = await Swal.fire({
      title: t('invites.confirm.cancelTitle'),
      text: t('invites.confirm.cancelText'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: t('invites.confirm.confirmBtn'),
      cancelButtonText: t('invites.confirm.cancelBtn'),
      confirmButtonColor: '#d33',
    });

    if (!resultado.isConfirmed) return;

    try {
      await cancelarConvite(conviteId);
      setConvites(prev => prev.filter(c => c.id !== conviteId));
      Swal.fire({
        icon: 'success',
        title: t('invites.success.cancelled'),
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error('Erro ao cancelar convite:', err);
      Swal.fire({
        icon: 'error',
        title: t('invites.titles.cancel'),
        text: err.data?.error || t('invites.errors.cancel'),
      });
    }
  };

  return {
    convites,
    carregando,
    erro,
    listarPorEvento,
    listarEnviados,
    listarMeus,
    enviar,
    responder,
    cancelar
  };
};
