<template>
  <div class="tipo-manager">
    <div class="manager-header">
      <h2>Gerenciar Tipos</h2>
      <button @click="fechar" class="btn-fechar">× Fechar</button>
    </div>

    <div class="tipo-form">
      <div class="form-group">
        <input
          v-model="form.nome"
          placeholder="Nome do tipo (ex: Fogo, Água)"
          @keyup.enter="salvarTipo"
          :disabled="salvando"
        >
      </div>

      <div class="form-actions">
        <button
          @click="salvarTipo"
          :disabled="!form.nome.trim() || salvando"
          class="btn-salvar"
        >
          {{ editandoId ? 'Atualizar' : 'Adicionar' }}
        </button>

        <button
          v-if="editandoId"
          @click="cancelarEdicao"
          class="btn-cancelar"
          :disabled="salvando"
        >
          Cancelar
        </button>
      </div>
    </div>

    <div class="tipos-lista">
      <div v-if="carregando" class="carregando">
        <div class="spinner"></div>
        <p>Carregando tipos...</p>
      </div>

      <div v-else-if="erro" class="erro">
        <p>{{ erro }}</p>
        <button @click="carregarTipos" class="btn-tentar">Tentar novamente</button>
      </div>

      <div v-else-if="tipos.length === 0" class="sem-tipos">
        <p>Nenhum tipo cadastrado.</p>
      </div>

      <div v-else class="lista-conteudo">
        <div
          v-for="tipo in tipos"
          :key="tipo.codigo"
          class="tipo-item"
          :class="{ editando: editandoId === tipo.codigo }"
        >
          <div class="tipo-info">
            <span class="tipo-nome">{{ tipo.nome }}</span>
            <span class="tipo-codigo">#{{ tipo.codigo }}</span>
          </div>

          <div class="tipo-acoes">
            <button
              @click="editarTipo(tipo)"
              class="btn-editar"
              :disabled="salvando"
            >
              Editar
            </button>
            <button
              @click="excluirTipo(tipo.codigo, tipo.nome)"
              class="btn-excluir"
              :disabled="salvando"
            >
              Excluir
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="mensagem" class="mensagem" :class="{ sucesso: mensagemSucesso, erro: !mensagemSucesso }">
      {{ mensagem }}
    </div>
  </div>
</template>

<script>
import tipoService from '../services/tipoService';
import pokemonService from '../services/pokemonService';

export default {
  name: 'TipoManager',
  emits: ['fechar', 'tipos-atualizados'],
  data() {
    return {
      tipos: [],
      form: {
        nome: ''
      },
      editandoId: null,
      carregando: false,
      salvando: false,
      erro: null,
      mensagem: null,
      mensagemSucesso: false
    };
  },
  methods: {
    async carregarTipos() {
      this.carregando = true;
      this.erro = null;

      try {
        console.log('Carregando tipos...');
        const response = await tipoService.getAll();
        this.tipos = response.data;
        console.log(`${this.tipos.length} tipos carregados`);
      } catch (error) {
        console.error(' Erro em carregar tipos:', error);
        this.erro = 'Falha em carregar tipos: ' + error.message;
      } finally {
        this.carregando = false;
      }
    },

    async salvarTipo() {
      if (!this.form.nome.trim()) {
        this.mostrarMensagem('Nome do tipo naõ pode estar vazio', false);
        return;
      }

      this.salvando = true;
      this.mensagem = null;

      try {
        if (this.editandoId) {
          console.log(` Atualizando tipo ${this.editandoId}: ${this.form.nome}`);
          await tipoService.update(this.editandoId, this.form);
          this.mostrarMensagem('Tipo atualizado com sucesso!', true);
        } else {
          console.log(`Crindo novo tipo: ${this.form.nome}`);
          await tipoService.create(this.form);
          this.mostrarMensagem('Tipo criado com sucesso!', true);
        }

        await this.carregarTipos();
        this.resetarFormulario();

        this.$emit('tipos-atualizados');

      } catch (error) {
        console.error(' Erro ao salvar tipo:', error);
        const mensagemErro = error.response?.data?.message || error.message;
        this.mostrarMensagem(`Erro: ${mensagemErro}`, false);
      } finally {
        this.salvando = false;
      }
    },

    editarTipo(tipo) {
      console.log('Editand tipo:', tipo);
      this.form.nome = tipo.nome;
      this.editandoId = tipo.codigo;
      this.mensagem = null;
    },

    async excluirTipo(id, nome) {
      if (!confirm(`Excluir tipo "${nome}"?`)) return;

      try {
        await tipoService.delete(id);
        alert(` Tipo "${nome}" excluído!`);
        await this.carregarTipos();
        this.$emit('tipos-atualizados');
      } catch (error) {
        alert(` Erro: Não foi possivel excluir. O tipo está sendo usado.`);
      }
    },

    cancelarEdicao() {
      this.resetarFormulario();
      this.mostrarMensagem('Edição cancelada', true);
    },

    resetarFormulario() {
      this.form.nome = '';
      this.editandoId = null;
    },

    mostrarMensagem(texto, sucesso) {
      this.mensagem = texto;
      this.mensagemSucesso = sucesso;

      setTimeout(() => {
        this.mensagem = null;
      }, 5000);
    },

    fechar() {
      this.$emit('fechar');
    }
  },
  mounted() {
    this.carregarTipos();
  }
};
</script>

<style scoped>
.tipo-manager {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin: 20px 0;
}

.manager-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.manager-header h2 {
  margin: 0;
  color: #333;
}

.btn-fechar {
  background: #f44336;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-fechar:hover {
  background: #d32f2f;
}

.tipo-form {
  margin-bottom: 25px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 6px;
}

.form-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  margin-bottom: 15px;
}

.form-group input:focus {
  outline: none;
  border-color: #4caf50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.form-actions {
  display: flex;
  gap: 10px;
}

.btn-salvar, .btn-cancelar, .btn-editar, .btn-excluir {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-salvar {
  background: #4caf50;
  color: white;
  flex: 2;
}

.btn-salvar:hover:not(:disabled) {
  background: #45a049;
}

.btn-cancelar {
  background: #ff9800;
  color: white;
  flex: 1;
}

.btn-cancelar:hover:not(:disabled) {
  background: #f57c00;
}

.btn-salvar:disabled, .btn-cancelar:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.tipos-lista {
  max-height: 400px;
  overflow-y: auto;
}

.carregando {
  text-align: center;
  padding: 40px;
  color: #666;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #4caf50;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.erro {
  text-align: center;
  padding: 20px;
  color: #f44336;
  background: #ffebee;
  border-radius: 4px;
}

.btn-tentar {
  margin-top: 10px;
  padding: 8px 16px;
  background: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.sem-tipos {
  text-align: center;
  padding: 40px;
  color: #999;
  font-style: italic;
}

.tipo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  transition: background 0.2s;
}

.tipo-item:hover {
  background: #f9f9f9;
}

.tipo-item.editando {
  background: #e8f5e9;
  border-left: 4px solid #4caf50;
}

.tipo-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.tipo-nome {
  font-weight: 600;
  color: #333;
  text-transform: capitalize;
  min-width: 120px;
}

.tipo-codigo {
  color: #666;
  font-size: 14px;
  background: #f0f0f0;
  padding: 2px 8px;
  border-radius: 12px;
}

.tipo-acoes {
  display: flex;
  gap: 8px;
}

.btn-editar {
  background: #2196f3;
  color: white;
}

.btn-editar:hover:not(:disabled) {
  background: #1976d2;
}

.btn-excluir {
  background: #f44336;
  color: white;
}

.btn-excluir:hover:not(:disabled) {
  background: #d32f2f;
}

.btn-editar:disabled, .btn-excluir:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.mensagem {
  margin-top: 20px;
  padding: 12px;
  border-radius: 4px;
  text-align: center;
  font-weight: 500;
}

.mensagem.sucesso {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
}

.mensagem.erro {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ffcdd2;
}
</style>
