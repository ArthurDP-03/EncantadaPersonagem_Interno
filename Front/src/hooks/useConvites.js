import { useState, useEffect } from 'react';
import Swal from 'sweetalert2';
import {
  enviarConvites,
  listarPorEventoPersonagem,
  listarEnviadosADM,
  listarMeusConvites,
  responderConvite,
  cancelarConvite,
  ConviteStatus
} from '../services/conviteService';

export const useConvite = () => {
  const [convites, setConvites] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  // Listar convites por evento-personagem
  const listarPorEvento = async (epId) => {
    try {
      setCarregando(true);
      const dados = await listarPorEventoPersonagem(epId);
      setConvites(dados);
      return dados;
    } catch (err) {
      console.error('Erro ao listar convites:', err);
      setErro(err.data?.error || 'Erro ao carregar convites');
      Swal.fire({
        icon: 'error',
        title: 'Erro ao carregar',
        text: err.data?.error || 'Não foi possível carregar os convites',
      });
    } finally {
      setCarregando(false);
    }
  };

  // Listar convites enviados pelo admin
  const listarEnviados = async () => {
    try {
      setCarregando(true);
      const dados = await listarEnviadosADM();
      setConvites(dados);
      return dados;
    } catch (err) {
      console.error('Erro ao listar convites enviados:', err);
      setErro(err.data?.error || 'Erro ao carregar convites enviados');
      Swal.fire({
        icon: 'error',
        title: 'Erro ao carregar',
        text: err.data?.error || 'Não foi possível carregar os convites',
      });
    } finally {
      setCarregando(false);
    }
  };

  // Listar meus convites (ator)
  const listarMeus = async () => {
    try {
      setCarregando(true);
      const dados = await listarMeusConvites();
      setConvites(dados);
      return dados;
    } catch (err) {
      console.error('Erro ao listar meus convites:', err);
      setErro(err.data?.error || 'Erro ao carregar seus convites');
      Swal.fire({
        icon: 'error',
        title: 'Erro ao carregar',
        text: err.data?.error || 'Não foi possível carregar seus convites',
      });
    } finally {
      setCarregando(false);
    }
  };

  // Enviar convites
  const enviar = async (eventoPersonagemId, atoresIds) => {
    try {
      const novosConvites = await enviarConvites({ eventoPersonagemId, atoresIds });
      setConvites(prev => [...prev, ...novosConvites]);
      Swal.fire({
        icon: 'success',
        title: 'Convites enviados',
        timer: 1800,
        showConfirmButton: false
      });
      return novosConvites;
    } catch (err) {
      console.error('Erro ao enviar convites:', err);
      Swal.fire({
        icon: 'error',
        title: 'Erro ao enviar',
        text: err.data?.error || 'Não foi possível enviar os convites',
      });
    }
  };

  // Responder convite
  const responder = async (conviteId, status) => {
    try {
      const atualizado = await responderConvite(conviteId, { status });
      setConvites(prev => prev.map(c => (c.id === conviteId ? atualizado : c)));
      
      const mensagem = status === ConviteStatus.ACEITO ? 'Convite aceito' : 'Convite recusado';
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
        title: 'Erro ao responder',
        text: err.data?.error || 'Não foi possível responder o convite',
      });
    }
  };

  // Cancelar convite
  const cancelar = async (conviteId) => {
    const resultado = await Swal.fire({
      title: 'Deseja cancelar este convite?',
      text: 'Esta ação não poderá ser desfeita.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Cancelar',
      cancelButtonText: 'Voltar',
      confirmButtonColor: '#d33',
    });

    if (!resultado.isConfirmed) return;

    try {
      await cancelarConvite(conviteId);
      setConvites(prev => prev.filter(c => c.id !== conviteId));
      Swal.fire({
        icon: 'success',
        title: 'Convite cancelado',
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error('Erro ao cancelar convite:', err);
      Swal.fire({
        icon: 'error',
        title: 'Erro ao cancelar',
        text: err.data?.error || 'Não foi possível cancelar o convite',
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