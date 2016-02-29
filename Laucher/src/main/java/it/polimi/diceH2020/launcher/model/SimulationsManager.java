package it.polimi.diceH2020.launcher.model;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.polimi.diceH2020.SPACE4Cloud.shared.settings.SolverType;
import it.polimi.diceH2020.SPACE4Cloud.shared.solution.Solution;
import it.polimi.diceH2020.launcher.utility.Compressor;

/**
 * This class contain informations about client's requested set of simulations
 */
@Entity
public class SimulationsManager {

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	@NotNull
	private Integer accuracy = 5;

	@OneToMany(mappedBy = "simulationsManager", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OrderColumn(name = "simManager_index")
	private List<InteractiveExperiment> lstExperiments = new ArrayList<InteractiveExperiment>();

	private String date;

	@Id
	@Column(name = "SIM_MANAGER")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Transient
	private Solution inputSolution;

	@Column(length = 1000)
	private String solution;

	@Transient
	private Integer maxNumUsers;
	@Transient
	private Integer maxNumVMs;
	@Transient
	private Integer minNumUsers;
	@Transient
	private Integer minNumVMs;

	private Integer numCompletedSimulations;

	private Integer numIter = 1;

	private Integer size;

	private String state = "ready";
	
	private SolverType solver = SolverType.QNSolver;

	@NotNull
	@Transient
	private Integer stepUsers = 1;

	@NotNull
	@Transient
	private Integer stepVMs = 1;

	@Transient
	private Integer thinkTime;
	private String time;

	private String instanceName = "";

	@Column(length = 200000)
	private String mapFile;

	@Column(length = 200000)
	private String rsFile;
	
	
	
	@NotNull
	@Min(60)
	private Integer simDuration = 180;

	public Integer getSimDuration() {
		return simDuration;
	}

	public void setSimDuration(Integer simDuration) {
		this.simDuration = simDuration;
	}

	public SimulationsManager() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		java.util.Date date = new java.util.Date();
		this.date = dateFormat.format(date);
		this.time = timeFormat.format(date);
	}

	public void buildExperiments() {
		this.lstExperiments.clear();
		for (int numVMs = minNumVMs; numVMs <= maxNumVMs; numVMs = numVMs + stepVMs)
			for (int numUsers = minNumUsers; numUsers <= maxNumUsers; numUsers = numUsers + stepUsers)
				for (int it = 1; it <= this.numIter; it++) {
					InteractiveExperiment experiment = new InteractiveExperiment();
					experiment.setIter(it);
					experiment.setNumUsers(numUsers);
					experiment.setNumVMs(numVMs);
					experiment.setThinkTime(this.thinkTime);
					experiment.setInstanceName(this.instanceName);
					experiment.setSimulationsManager(this);
					this.lstExperiments.add(experiment);
				}
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public List<InteractiveExperiment> getClassList() {
		return lstExperiments;
	}

	public String getDate() {
		return date;
	}

	public Long getId() {
		return id;
	}

	public Solution getInputSolution() {
		if (inputSolution != null) {
			return inputSolution;
		} else if (solution != null) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				return this.solution.equals("") || this.solution.equals("Error") ? null : mapper.readValue(Compressor.decompress(this.solution), Solution.class);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return inputSolution;
	}

	public Integer getMaxNumUsers() {
		return maxNumUsers;
	}

	public Integer getMaxNumVMs() {
		return maxNumVMs;
	}

	public Integer getMinNumUsers() {
		return minNumUsers;
	}

	public Integer getMinNumVMs() {
		return minNumVMs;
	}

	public Integer getNumCompletedSimulations() {
		return numCompletedSimulations;
	}

	public Integer getNumIter() {
		return numIter;
	}

	public int getSize() {
		this.size = lstExperiments.size();
		return size;
	}

	public String getState() {
		return state;
	}

	public Integer getStepUsers() {
		return stepUsers;
	}

	public Integer getStepVMs() {
		return stepVMs;
	}

	public Integer getThinkTime() {
		return thinkTime;
	}

	public String getTime() {
		return time;
	}

	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}

	public void setClassList(List<InteractiveExperiment> simulationsList) {
		this.lstExperiments = simulationsList;
	}

	public void setDate(String date) {
		this.date = date;
	}


	public void setId(Long id) {
		this.id = id;
	}

	public void setInputSolution(Solution inputSolution) {
		this.inputSolution = inputSolution;

		ObjectMapper mapper = new ObjectMapper();
		try {
			this.solution = Compressor.compress(mapper.writeValueAsString(inputSolution));
		} catch (IOException e) {
			this.solution = "Error";
		}
		Double tt = inputSolution.getLstSolutions().get(0).getJob().getThink();
		this.thinkTime = tt.intValue();
		this.instanceName = inputSolution.getId();
	}

	public void setMaxNumUsers(Integer maxNumUsers) {
		this.maxNumUsers = maxNumUsers;
	}

	public void setMaxNumVMs(Integer maxNumCores) {
		this.maxNumVMs = maxNumCores;
	}

	public void setMinNumUsers(Integer minUsers) {
		this.minNumUsers = minUsers;
	}

	public void setMinNumVMs(Integer minCores) {
		this.minNumVMs = minCores;
	}

	public void setNumCompletedSimulations(Integer numCompletedSimulations) {
		this.numCompletedSimulations = numCompletedSimulations;
	}

	public void setNumIter(Integer numIter) {
		this.numIter = numIter;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setStepUsers(Integer stepUsrs) {
		this.stepUsers = stepUsrs;
	}

	public void setStepVMs(Integer stepVMs) {
		this.stepVMs = stepVMs;
	}

	public void setThinkTime(Integer thinkTime) {
		this.thinkTime = thinkTime;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMapFile() {
		try {
			return Compressor.decompress(mapFile);
		} catch (IOException e) {
			return "";
		}
	}

	public void setMapFile(String mapFile) {
		try {
			this.mapFile = Compressor.compress(mapFile);
		} catch (IOException e) {
			this.mapFile = "";
		}
	}

	public void setRsFile(String rsFile) {
		try {
			this.rsFile = Compressor.compress(rsFile);
		} catch (IOException e) {
			this.rsFile = "";
		}

	}

	public String getRsFile() {
		try {
			return Compressor.decompress(rsFile);
		} catch (IOException e) {
			return "";
		}
	}

	public SolverType getSolver() {
		return solver;
	}

	public void setSolver(SolverType solver) {
		this.solver = solver;
	}

}
