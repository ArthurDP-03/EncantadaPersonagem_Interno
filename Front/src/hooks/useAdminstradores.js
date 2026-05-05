import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import { getAdministrador, deletarAdministrador, criarAdministrador, atualizarAdministrador } from "../services/administradoresService";

export function useAdministradores() {
  const [administradores, setAdministradores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAdministrador()
      .then(setAdministradores)
      .catch(err => {
        console.error("Erro completo:", err);
        setErro(err.data?.error || "Erro ao carregar administradores");
        Swal.fire({ icon: "error", title: "Erro ao carregar", text: err.data?.error || "Não foi possível carregar os administradores" });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({ title: "Deseja deletar este administrador?", text: "Esta ação não poderá ser desfeita.", icon: "warning", showCancelButton: true, confirmButtonText: "Deletar", cancelButtonText: "Cancelar", confirmButtonColor: "#d33" });
    if (!resultado.isConfirmed) return;
    try {
      await deletarAdministrador(id);
      setAdministradores(prev => prev.filter(a => a.id !== id));
      Swal.fire({ icon: "success", title: "Administrador deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar administrador:", err);
      Swal.fire({ icon: "error", title: "Erro ao deletar", text: err.data?.error || "Não foi possível deletar o administrador" });
    }
  };

  const criar = async (dados) => {
    try {
      const novoAdministrador = await criarAdministrador(dados);
      setAdministradores(prev => [...prev, novoAdministrador]);
      Swal.fire({ icon: "success", title: "Administrador criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar administrador:", err);
      Swal.fire({ icon: "error", title: "Erro ao criar", text: err.data?.error || "Não foi possível criar o administrador" });
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarAdministrador(id, dados);
      setAdministradores(prev => prev.map(a => a.id === id ? atualizado : a));
      Swal.fire({ icon: "success", title: "Administrador atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar administrador:", err);
      Swal.fire({ icon: "error", title: "Erro ao atualizar", text: err.data?.error || "Não foi possível atualizar o administrador" });
    }
  };

  return { administradores, carregando, erro, deletar, criar, editar };
}